package net.sayaya.data;

import net.sayaya.ui.event.HasValueChangeHandlers;

import java.util.function.Function;

public interface Observable<T> extends HasValueChangeHandlers<T> {
    void fireValueChangeEvent();
    static <A, B> void wire(Observable<A> source, Function<A, B> mapper, ObservableSingle<B> sink) {
        source.onValueChange(evt->sink.value(mapper.apply(evt.value())));
    }
    interface ObservableSingle<T> extends Observable<T> {
        void value(T value);
    }
    interface ObservableMany<T> extends Observable<T[]> {
        void add(T value);
        void remove(T value);
    }
}
