package net.rabbitknight.open.media.core

/**
 * Project All Exception Parent
 */
open class MediaException : RuntimeException {
    constructor() : super() {}

    constructor(message: String?) : super(message) {}

    constructor(message: String?, cause: Throwable?) : super(message, cause) {}

    constructor(cause: Throwable?) : super(cause) {}
}