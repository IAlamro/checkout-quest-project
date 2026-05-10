package com.checkout.shared.cqrs;

public interface CommandHandler<C extends Command, R> {
    R handle(C command);
}
