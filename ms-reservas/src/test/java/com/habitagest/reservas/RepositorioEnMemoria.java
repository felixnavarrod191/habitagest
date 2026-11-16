package com.habitagest.reservas;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio en memoria para las pruebas: implementa save, findById, findAll, existsById y las consultas derivadas
 * findBy..., existsBy... y countBy... (con "And"), sin necesitar MongoDB.
 */
final class RepositorioEnMemoria {

    private RepositorioEnMemoria() {
    }

    @SuppressWarnings("unchecked")
    static <R> R crear(Class<R> tipo) {
        Map<String, Object> datos = new LinkedHashMap<>();
        AtomicLong secuencia = new AtomicLong();
        InvocationHandler manejador = (proxy, metodo, argumentos) -> {
            String nombre = metodo.getName();
            switch (nombre) {
                case "save":
                    return guardar(datos, secuencia, argumentos[0]);
                case "findById":
                    return Optional.ofNullable(datos.get((String) argumentos[0]));
                case "findAll":
                    return new ArrayList<>(datos.values());
                case "existsById":
                    return datos.containsKey((String) argumentos[0]);
                case "count":
                    return (long) datos.size();
                case "deleteAll":
                    datos.clear();
                    return null;
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == argumentos[0];
                case "toString":
                    return "RepositorioEnMemoria";
                default:
                    break;
            }
            if (nombre.startsWith("findBy") || nombre.startsWith("existsBy") || nombre.startsWith("countBy")) {
                return consultar(datos, metodo, argumentos);
            }
            throw new UnsupportedOperationException(nombre);
        };
        return (R) Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[] {tipo}, manejador);
    }

    private static Object guardar(Map<String, Object> datos, AtomicLong secuencia, Object entidad) throws Exception {
        Method leerId = entidad.getClass().getMethod("getId");
        Object id = leerId.invoke(entidad);
        if (id == null) {
            id = String.valueOf(secuencia.incrementAndGet());
            entidad.getClass().getMethod("setId", String.class).invoke(entidad, id);
        }
        datos.put((String) id, entidad);
        return entidad;
    }

    private static Object consultar(Map<String, Object> datos, Method metodo, Object[] argumentos) throws Exception {
        String nombre = metodo.getName();
        String prefijo = nombre.startsWith("findBy") ? "findBy" : nombre.startsWith("existsBy") ? "existsBy" : "countBy";
        String[] propiedades = nombre.substring(prefijo.length()).split("And");
        List<Object> coincidencias = new ArrayList<>();
        for (Object entidad : datos.values()) {
            boolean coincide = true;
            for (int i = 0; i < propiedades.length; i++) {
                Object valor = entidad.getClass().getMethod("get" + propiedades[i]).invoke(entidad);
                if (!Objects.equals(valor, argumentos[i])) {
                    coincide = false;
                    break;
                }
            }
            if (coincide) {
                coincidencias.add(entidad);
            }
        }
        Class<?> retorno = metodo.getReturnType();
        if (retorno == boolean.class) {
            return !coincidencias.isEmpty();
        }
        if (retorno == long.class) {
            return (long) coincidencias.size();
        }
        if (retorno == Optional.class) {
            return coincidencias.isEmpty() ? Optional.empty() : Optional.of(coincidencias.get(0));
        }
        if (retorno == List.class) {
            return coincidencias;
        }
        return coincidencias.isEmpty() ? null : coincidencias.get(0);
    }
}
