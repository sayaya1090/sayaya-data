package net.sayaya.data;

import net.sayaya.ui.event.HasValueChangeHandlers;

public interface Observable<T> extends HasValueChangeHandlers<T> {
    void fireValueChangeEvent();
    interface ObservableSingle<T> extends Observable<T> {
        void value(T value);
    }
    interface ObservableMany<T> extends Observable<T[]> {
        void add(T value);
        void remove(T value);
    }
}
