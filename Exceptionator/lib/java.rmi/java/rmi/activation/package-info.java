/*
 * Copyright (c) 1998, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/**
 * Provides support for RMI Object Activation.  A remote
 * object's reference can be made ``persistent'' and later activated into a
 * ``live'' object using the RMI activation mechanism.
 *
 * <p>Implementations are not required to support the activation
 * mechanism. If activation is not supported by this implementation,
 * several specific activation API methods are all required to throw
 * {@code UnsupportedOperationException}. If activation is supported by this
 * implementation, these methods must never throw {@code
 * UnsupportedOperationException}. These methods are denoted by the
 * presence of an entry for {@code UnsupportedOperationException} in the
 * <strong>Throws</strong> section of each method's specification.
 *
 * @since 1.2
 */
package java.rmi.activation;
