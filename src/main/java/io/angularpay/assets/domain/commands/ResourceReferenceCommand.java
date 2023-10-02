package io.angularpay.assets.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}
