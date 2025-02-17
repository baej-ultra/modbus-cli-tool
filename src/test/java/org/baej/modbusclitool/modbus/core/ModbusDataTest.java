package org.baej.modbusclitool.modbus.core;

class ModbusDataTest {

//    @Test
//    void getValues() {
//        Map<Integer, Number> map = new TreeMap<>(
//                Map.of(0, 1.2f,
//                        4, 2.66f,
//                        8, 10.66f)
//        );
//        byte[] bytes = {(byte) 0x3F, (byte) 0x99, (byte) 0x99, (byte) 0x9A, (byte) 0x40, (byte) 0x2A, (byte) 0x3D, (byte) 0x71,
//                (byte) 0x41, (byte) 0x2A, (byte) 0x66, (byte) 0x66};
//        ModbusData md = new ModbusData(bytes, ModbusDataDisplayFormat.FLOAT, ModbusDataByteOrder.BIG_ENDIAN);
//
//
//        assertEquals(3, md.getValues().size());
//        for (var entry : md.getValues().entrySet()) {
//            var val1 = (float) entry.getValue();
//            var val2 = (float) map.get(entry.getKey());
//            assertEquals(val2, val1, 0.05f);
//        }
//    }
}