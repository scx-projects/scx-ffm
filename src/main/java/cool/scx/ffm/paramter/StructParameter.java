package cool.scx.ffm.paramter;

import cool.scx.ffm.struct.Struct;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static cool.scx.ffm.FFMHelper.getMemoryLayout;

/// StructParameter
/// todo 目前 只支持单层的 结构 需要支持多层
///
/// @author scx567888
/// @version 0.0.1
public class StructParameter implements Parameter {

    private final Object value;
    private final Map<Field, VarHandle> fieldMap;
    private final StructLayout LAYOUT;
    private MemorySegment memorySegment;

    public StructParameter(Struct value) {
        this.value = value;
        var classInfo = this.value.getClass();
        this.fieldMap = new HashMap<>();
        //1, 寻找 public 的 fields
        var fields = classInfo.getFields();

        //2, 创建每个 field 对应的 内存布局
        var memoryLayouts = new MemoryLayout[fields.length];
        for (int i = 0; i < fields.length; i = i + 1) {
            var f = fields[i];
            var memoryLayout = getMemoryLayout(f.getType());
            memoryLayouts[i] = memoryLayout.withName(f.getName());
        }

        this.LAYOUT = MemoryLayout.structLayout(memoryLayouts);

        //3, 创建 varHandle 以便能反向将 内存中的值读取出来
        for (var f : fields) {
            var x = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(f.getName()));
            var ff = MethodHandles.insertCoordinates(x, 1, 0L);
            fieldMap.put(f, ff);
        }
    }

    @Override
    public Object toNativeParameter(Arena arena) {
        return this.memorySegment = arena.allocate(LAYOUT);
    }

    @Override
    public void beforeCloseArena() {
        for (var e : fieldMap.entrySet()) {
            var k = e.getKey();
            var v = e.getValue();
            var o = v.get(memorySegment);
            try {
                k.set(this.value, o);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
