package net.sayaya.data;

public class Proxy {
    private native static <T> T make(T origin, ChangeHandler<T> consumer) /*-{
		var proxy = new Proxy(origin, {
		set: function(target, key, value, receiver) {
				if(target[key]==value) return true;
				var result = Reflect.set(target, key, value, receiver);
				if(result) consumer.@net.sayaya.data.Proxy.ChangeHandler::onInvokeAny(Ljava/lang/Object;)(target);
				return result;
			}
		});
		return proxy;
	}-*/;
    static <T extends Observable<?>> T make(T observable) {
        return Proxy.make(observable, T::fireValueChangeEvent);
    }
    interface ChangeHandler<T> {
        void onInvoke(T data);
        default void onInvokeAny(Object data) {
            onInvoke((T)data);
        }
    }
}
