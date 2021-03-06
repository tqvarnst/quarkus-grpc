package io.quarkus.grpc.runtime.annotations;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Qualifier used to inject a GRPC Service (to consume it).
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, CONSTRUCTOR, FIELD, PARAMETER })
public @interface GrpcService {

    /**
     * @return the configuration key to configure the GRPC client, such as the location, the communication
     * characteristics...
     */
    String value();

}
