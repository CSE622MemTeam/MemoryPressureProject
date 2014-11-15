import java.io.*;
import java.lang.reflect.*;

import android.os.*;
import java.util.*;

class Bundle extends HashMap<String, Object> {
	static Bundle EMPTY = new Bundle();
}

/**
 * Class for automatically bundling and unbundling objects.
 */
public final class Bundler {
	/** Utility class; don't allow instantiation... */
	private Bundler() { }

	/** Create a bundle from an object. */
	public static Bundle bundle(Object object) {
		return bundle(Object.class, object);
	}

	/** Create a bundle from an object, with the given base class. */
	public static Bundle bundle(Class<?> base, Object object) {
		return new MLBundler(base).bundle(object);
	}

	/** Unbundle a bundle back into the original object. */
	public static Object unbundle(Bundle bundle) {
		return null; //return new MLUnbundler(bundle).unbundle();
	}

	/** Unbundle a bundle back into the given object. */
	public static <T> T unbundle(Bundle bundle, T object) {
		return null; //return new MLUnbundler(bundle).unbundle(object);
	}

	public static void main(String[] args) {
		Bundle b = bundle(System.out);
		System.out.println(b);
	}
}

/** Put utility methods here until we can find a better home. */
class Util {
	/** Used to fix primitive classes. */
	static final Map<Class,Class> PRIM_MAP = new HashMap<Class,Class>() {{
		put(boolean.class, Boolean.class);
		put(char.class, Character.class);
		put(byte.class, Byte.class);
		put(short.class, Short.class);
		put(int.class, Integer.class);
		put(long.class, Long.class);
		put(float.class, Float.class);
		put(double.class, Double.class);
	}};

	static Class<?> normalizeType(Class<?> clazz) {
		if (clazz.isPrimitive())
			clazz = PRIM_MAP.get(clazz);
		return clazz;
	}

	/** Check if serializable, or array of serializables. */
	static boolean isSerializable(Object o) {
		Class<?> component = o.getClass();

		// Find component type, if array.
		while (component.isArray())
			component = component.getComponentType();

		return Serializable.class.isAssignableFrom(component);
	}

	static boolean shouldIgnoreField(Field f) {
		int mod = 0;//f.getModifiers();
		return Modifier.isTransient(mod) || Modifier.isStatic(mod);
	}
}

/**
 * A wrapper around a Bundle that provides some nicer methods. This is done as
 * a decorator because Bundle is final. This should be used only by MLBundler
 * and MLUnbundler below.
 */
final class MLBundle {
	final Bundle bundle;

	MLBundle() {
		this(new Bundle());
	}

	MLBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Add an object to the bundle. This calls the appropriate bundle method
	 * based on object type. Returns the encapsulated Bundle.
	 */
	Bundle put(String key, Object o) {
		if (o == null)
			return bundle;
		//else if (o instanceof Parcelable)
			//bundle.putParcelable(key, (Parcelable) o);
		else if (Util.isSerializable(o))
			//bundle.putSerializable(key, (Serializable) o);
			bundle.put(key, (Serializable) o);
		else
			throw new RuntimeException("Unable to marshal: "+o);
		return bundle;
	}
}

/** Class to convert an object into a custom bundle. */
class MLBundler {
	/** Path of this bundler in the hierarchy. */
	protected String path = "<root>";
	/** Top-most class to marshal. */
	protected Class<?> base;
	/** Class we're currently marshalling. */
	protected Class<?> clazz;
	/** Bundle we're currently working with. */
	private MLBundle bundle = new MLBundle();
	/** Map of visited objects. For loop detection. */
	protected Map<Object,String> visitMap = new HashMap<Object,String>();

	/** Create a bundler that will marshal everything. */
	public MLBundler() { this(Object.class); }

	/** Create a bundler that will not marshal past base. */
	public MLBundler(Class<?> base) { this.base = base; }

	/** A sub-bundler for bundling field values. */
	class SubMLBundler extends MLBundler {
		public SubMLBundler(String name, Class<?> clazz) {
			super(MLBundler.this.base);
			path = MLBundler.this.path+"."+name;
			this.clazz = clazz;
			visitMap = MLBundler.this.visitMap;
		}
	}

	/** Bundle the given object. */
	public Bundle bundle(Object object) {
		if (object == null)
			return Bundle.EMPTY;

		if (clazz == null)
			clazz = object.getClass();

		String path = visitMap.get(new VKey(object));

		if (path != null)
			return bundle.put("<ref>", path);

		return visit(object);
	}

	/** Visit an object for the first time. */
	private Bundle visit(Object object) {
		visitMap.put(new VKey(object), path);

		if (Util.isSerializable(object))
			return bundle.put("<data>", object);

		bundle.put("<class>", object.getClass());

		return putFields(object);
	}

	/** Bundle the fields of an object by name. */
	private Bundle putFields(Object object) {
		for (Field field : clazz.getDeclaredFields())
			putField(object, field);
		return putSuperclassFields(object);
	}

	/** Bundle a single field. */
	private void putField(Object object, Field field) {
		boolean access = field.isAccessible();

		if (!Util.shouldIgnoreField(field)) try {
			field.setAccessible(true);

			String key = field.getName();
			Object value = field.get(object);

			if (value == null)
				return;

			Bundle sb = new SubMLBundler(key, null).bundle(value);

			bundle.put(key, sb);
		} catch (Exception e) {
			// SecurityManager doesn't let us do this. What can we do?
		} finally {
			field.setAccessible(access);
		}
	}

	/** Bundle all the fields in the superclass. */
	private Bundle putSuperclassFields(Object object) {
		Class<?> sc = clazz.getSuperclass();
		if (!classIsAllowed(sc))
			return bundle.bundle;
		Bundle sb = new SubMLBundler("<super>", sc).bundle(object);
		return bundle.put("<super>", sb);
	}

	/**
	 * Make sure we're only bundling allowed classes. The first check ensures
	 * that clazz is a type of base, and the second check ensures that clazz is a
	 * strict subtype of base.
	 */
	private boolean classIsAllowed(Class<?> clazz) {
		return base.isAssignableFrom(clazz) && !clazz.isAssignableFrom(base);
	}
}

/** Wrapper which compares object identity. Used to detect loops. */
final class VKey {
	final Object object;

	public VKey(Object object) { this.object = object; }

	public boolean equals(Object o) {
		return (o instanceof VKey) && object == ((VKey) o).object;
	}

	public int hashCode() {
		return System.identityHashCode(object);
	}
}

/** A reference to an object in a bundle. */
class BundleReference {
	String path;
	BundleReference(String path) { this.path = path; }
}
