package net.sayaya.data;

import elemental2.core.JsArray;
import elemental2.dom.CustomEvent;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import net.sayaya.data.Observable.ObservableMany;
import net.sayaya.data.Observable.ObservableSingle;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.HashMap;
import java.util.Map;

public class Holder {
    private final static Map<Class<?>, Observable<?>> observables = new HashMap<>();
    public static <V> void createSingle(Class<V> clazz) {
        create(clazz, new ObservableSingleImpl<V>());
    }
    public static <V> void createMany(Class<V> clazz) {
        create(clazz, new ObservableManyImpl<V>());
    }
    private static <V, T extends Observable<?>> void create(Class<V> clazz, T observable) {
        if(observables.containsKey(clazz)) throw new RuntimeException("Already exists exception:" + clazz);
        T proxy = Proxy.make(observable);
        observables.put(clazz, proxy);
    }
    @SuppressWarnings("unchecked")
    public static <V> Observable<V> any(Class<V> clazz) {
        if(observables.containsKey(clazz)) return (Observable<V>) observables.get(clazz);
        return null;
    }
    @SuppressWarnings("unchecked")
    public static <V> ObservableSingle<V> single(Class<V> clazz) {
        if(observables.containsKey(clazz)) {
            var observable = observables.get(clazz);
            if(observable instanceof ObservableSingle<?>) return (ObservableSingle<V>) observable;
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public static <V> ObservableMany<V> many(Class<V> clazz) {
        if(observables.containsKey(clazz)) {
            var observable = observables.get(clazz);
            if(observable instanceof ObservableMany<?>) return (ObservableMany<V>) observable;
        }
        return null;
    }
    @JsType
    private static class ObservableSingleImpl<T> implements ObservableSingle<T> {
        private T value;
        @JsIgnore
        private final JsArray<ValueChangeEventListener<T>> valueChangeListeners = JsArray.of();
        @JsMethod
        public void fireValueChangeEvent() {
            ValueChangeEvent<T> evt = ValueChangeEvent.event(new CustomEvent<>("change"), value());
            for (var listener : valueChangeListeners.asList()) {
                if (listener == null) break;
                listener.handle(evt);
            }
        }
        @Override @JsMethod(name="getValue")
        public T value() {
            return value;
        }
        @Override @JsMethod(name="setValue")
        public void value(T value) {
            this.value = value;
        }
        @Override @JsMethod
        public HandlerRegistration onValueChange(ValueChangeEventListener<T> listener) {
            //noinspection unchecked
            valueChangeListeners.push(listener);
            return () -> valueChangeListeners.delete(valueChangeListeners.asList().indexOf(listener));
        }
    }
    @JsType
    private static class ObservableManyImpl<T> implements ObservableMany<T> {
        private JsArray<T> values = JsArray.of();
        @JsIgnore
        private final JsArray<ValueChangeEventListener<T[]>> valueChangeListeners = JsArray.of();
        @JsMethod
        public void fireValueChangeEvent() {
            ValueChangeEvent<T[]> evt = ValueChangeEvent.event(new CustomEvent<>("change"), value());
            for (var listener : valueChangeListeners.asList()) {
                if (listener == null) break;
                listener.handle(evt);
            }
        }
        @Override @JsMethod(name="getValue")
        public T[] value() {
            return values.reverse();
        }
        @Override
        public void add(T value) {
            values.push(value);
        }
        @Override
        public void remove(T value) {
            values.delete(values.indexOf(value));
        }
        @Override @JsMethod
        public HandlerRegistration onValueChange(ValueChangeEventListener<T[]> listener) {
            //noinspection unchecked
            valueChangeListeners.push(listener);
            return () -> valueChangeListeners.delete(valueChangeListeners.asList().indexOf(listener));
        }
    }
}
