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

@SuppressWarnings("unchecked")
public class Holder {
    private final static Map<String, Observable<?>> observables = new HashMap<>();
    public static <V> ObservableSingle<V> createSingle(String key) {
        return create(key, new ObservableSingleImpl<V>());
    }
    public static <V> ObservableMany<V> createMany(String key) {
        return create(key, new ObservableManyImpl<V>());
    }
    private static <T extends Observable<?>> T create(String key, T observable) {
        if(observables.containsKey(key)) throw new RuntimeException("Already exists exception:" + key);
        T proxy = Proxy.make(observable);
        observables.put(key, proxy);
        return proxy;
    }
    public static <V> Observable<V> any(String key) {
        if(observables.containsKey(key)) return (Observable<V>) observables.get(key);
        return null;
    }
    public static <V> ObservableSingle<V> single(String key) {
        if(observables.containsKey(key)) {
            var observable = observables.get(key);
            if(observable instanceof ObservableSingle<?>) return (ObservableSingle<V>) observable;
        }
        return null;
    }
    public static <V> ObservableMany<V> many(String key) {
        if(observables.containsKey(key)) {
            var observable = observables.get(key);
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
        @Override @JsMethod(name="setValue")
        public void value(T[] values) {
            this.values = JsArray.of(values);
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
            valueChangeListeners.push(listener);
            return () -> valueChangeListeners.delete(valueChangeListeners.asList().indexOf(listener));
        }
    }
}
