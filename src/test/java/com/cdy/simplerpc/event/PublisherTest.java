package com.cdy.simplerpc.event;

public class PublisherTest {
    
    public static void main(String[] args) {
        Publisher publisher = new Publisher();
        publisher.registry(new RPCEventListener1());
        publisher.registry(new RPCEventListener<RPCInvokeEvent>() {
            @Override
            public void handle(RPCInvokeEvent eventObject) {
            
            }
        });
        publisher.registry((RPCEventListener<RPCInvokeEvent>) eventObject -> {
        
        });
    }
    
    
    static class RPCEventListener1 implements RPCEventListener<RPCInvokeEvent> {
        @Override
        public void handle(RPCInvokeEvent eventObject) {
            System.out.println("123");
        }
    }
    
}