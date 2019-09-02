package algorithm.diffutil.callback.listupdate

import algorithm.diffutil.ListUpdateCallback

class LoggingUpdateCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
        println("onInserted : position = [${position}], count = [${count}]")
    }

    override fun onRemoved(position: Int, count: Int) {
        println("onRemoved : position = [${position}], count = [${count}]")
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        println("onMoved : fromPosition = [${fromPosition}], toPosition = [${toPosition}]")
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        println("onChange: position = [${position}], count = [${count}], payload = [${payload}]")
    }
}