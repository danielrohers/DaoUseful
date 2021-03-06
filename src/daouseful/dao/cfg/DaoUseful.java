package daouseful.dao.cfg;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel Röhers Moura
 */
public class DaoUseful {
    
    /**
     * 
     * @param <T>
     * @param baseClass
     * @param childClass
     * @return 
     */
    public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass,
                    Class<? extends T> childClass) {
        Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (!getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just
                // keep going.
                type = ((Class) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                Type[] actualTypeArguments = parameterizedType
                                .getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                        resolvedTypes
                                        .put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                        type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass,
        // determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments;
        if (type instanceof Class) {
                actualTypeArguments = ((Class) type).getTypeParameters();
        } else {
                actualTypeArguments = ((ParameterizedType) type)
                                .getActualTypeArguments();
        }
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments) {
                while (resolvedTypes.containsKey(baseType)) {
                        baseType = resolvedTypes.get(baseType);
                }
                typeArgumentsAsClasses.add(getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }    

    /**
     * 
     * @param type
     * @return 
     */
    private static Class<?> getClass(Type type) {
        if (type instanceof Class) {
                return (Class) type;
        } else if (type instanceof ParameterizedType) {
                return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type)
                            .getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                    return Array.newInstance(componentClass, 0).getClass();
            } else {
                    return null;
            }
        } else {
            return null;
        }
    }        
    
}
