package net.rabbitknight.open.scanner.core.engine

import net.rabbitknight.open.scanner.core.format.BarFormat

interface Engine {
    /**
     * 是否引擎支持format
     */
    fun supportFormat(formalBarFormat: BarFormat): Boolean
}