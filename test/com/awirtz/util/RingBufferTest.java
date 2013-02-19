package com.awirtz.util;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RingBufferTest {
    
    public RingBufferTest() {}
    
    @Test public void testRingBuffer1() {
        System.out.println("new RingBuffer(int)");
        RingBuffer instance = new RingBuffer(3);
        assertEquals("[0, 0, 0]", Arrays.toString(instance.getByteArray()));
        assertEquals(0, instance.getTail());
        assertEquals(0, instance.getLength());
        assertEquals(3, instance.getMaxLength());
    }
    @Test public void testRingBuffer2() {
        System.out.println("new RingBuffer(byte[])");
        byte[] buffer = new byte[3];
        RingBuffer instance = new RingBuffer(buffer);
        assertEquals(buffer, instance.getByteArray());
        assertEquals(0, instance.getTail());
        assertEquals(0, instance.getLength());
        assertEquals(3, instance.getMaxLength());
    }
    @Test public void testRingBuffer3() {
        System.out.println("new RingBuffer(byte[],int,int)");
        byte[] buffer = new byte[3];
        RingBuffer instance = new RingBuffer(buffer, 1, 2);
        assertEquals(buffer, instance.getByteArray());
        assertEquals(1, instance.getTail());
        assertEquals(2, instance.getLength());
        assertEquals(3, instance.getMaxLength());
    }
    
    @Test public void testWrite1() {
        System.out.println("write(byte[],int,int)  [non-wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 0, 0);
        int result = instance.write(buffer, 1, 2);
        assertEquals(2, result);
        assertEquals("[2, 3, 0]", Arrays.toString(instance.getByteArray()));
        assertEquals(0, instance.getTail());
        assertEquals(2, instance.getLength());
    }
    @Test public void testWrite2() {
        System.out.println("write(byte[],int,int)  [offset non-wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 1, 0);
        int result = instance.write(buffer, 1, 2);
        assertEquals(2, result);
        assertEquals("[0, 2, 3]", Arrays.toString(instance.getByteArray()));
        assertEquals(1, instance.getTail());
        assertEquals(2, instance.getLength());
    }
    @Test public void testWrite3() {
        System.out.println("write(byte[],int,int)  [full non-wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 0, 0);
        int result = instance.write(buffer, 1, 3);
        assertEquals(3, result);
        assertEquals("[2, 3, 4]", Arrays.toString(instance.getByteArray()));
        assertEquals(0, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test public void testWrite4() {
        System.out.println("write(byte[],int,int)  [over-full non-wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 0, 0);
        int result = instance.write(buffer, 1, 4);
        assertEquals(3, result);
        assertEquals("[2, 3, 4]", Arrays.toString(instance.getByteArray()));
        assertEquals(0, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test public void testWrite5() {
        System.out.println("write(byte[],int,int)  [offset wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 2, 0);
        int result = instance.write(buffer, 1, 2);
        assertEquals(2, result);
        assertEquals("[3, 0, 2]", Arrays.toString(instance.getByteArray()));
        assertEquals(2, instance.getTail());
        assertEquals(2, instance.getLength());
    }
    @Test public void testWrite6() {
        System.out.println("write(byte[],int,int)  [full wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 2, 0);
        int result = instance.write(buffer, 1, 3);
        assertEquals(3, result);
        assertEquals("[3, 4, 2]", Arrays.toString(instance.getByteArray()));
        assertEquals(2, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test public void testWrite7() {
        System.out.println("write(byte[],int,int)  [overrun wrapping]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 2, 0);
        int result = instance.write(buffer, 1, 4);
        assertEquals(3, result);
        assertEquals("[3, 4, 2]", Arrays.toString(instance.getByteArray()));
        assertEquals(2, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test public void testWrite8() {
        System.out.println("write(byte[],int,int)  [prefilled wrapping]");
        byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {0,6,0};
        RingBuffer instance = new RingBuffer(prefill, 1, 1);
        int result = instance.write(buffer, 1, 2);
        assertEquals(2, result);
        assertEquals("[3, 6, 2]", Arrays.toString(instance.getByteArray()));
        assertEquals(1, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test public void testWrite9() {
        System.out.println("write(byte[],int,int)  [prefilled overrun wrapping]");
        byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {0,6,0};
        RingBuffer instance = new RingBuffer(prefill, 1, 1);
        int result = instance.write(buffer, 1, 3);
        assertEquals(2, result);
        assertEquals("[3, 6, 2]", Arrays.toString(instance.getByteArray()));
        assertEquals(1, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test public void testWrite10() {
        System.out.println("write(byte[],int,int)  [prefilled full]");
        byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {6,7,8};
        RingBuffer instance = new RingBuffer(prefill, 0, 3);
        int result = instance.write(buffer, 1, 3);
        assertEquals(0, result);
        assertEquals("[6, 7, 8]", Arrays.toString(instance.getByteArray()));
        assertEquals(0, instance.getTail());
        assertEquals(3, instance.getLength());
    }
    @Test(expected=IndexOutOfBoundsException.class) public void testWrite11() {
        System.out.println("write(byte[],int,int)  [out-of-bounds]");
        byte[] buffer = {1,2,3,4,5};
        RingBuffer instance = new RingBuffer(new byte[3], 2, 0);
        instance.write(buffer, 3, 3);
    }

    @Test public void testRead1() {
        System.out.println("read(byte[],int,int)  [non-wrapping]");
        byte[] prefill = {1,2,3};
        RingBuffer instance = new RingBuffer(prefill, 0, 3);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 2);
        assertEquals(2, result);
        assertEquals("[1, 2, 0]", Arrays.toString(buffer));
        assertEquals(2, instance.getTail());
        assertEquals(1, instance.getLength());
    }
    @Test public void testRead2() {
        System.out.println("read(byte[],int,int)  [offset non-wrapping]");
        byte[] prefill = {3,1,2};
        RingBuffer instance = new RingBuffer(prefill, 1, 3);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 1, 2);
        assertEquals(2, result);
        assertEquals("[0, 1, 2]", Arrays.toString(buffer));
        assertEquals(0, instance.getTail());
        assertEquals(1, instance.getLength());
    }
    @Test public void testRead3() {
        System.out.println("read(byte[],int,int)  [full non-wrapping]");
        byte[] prefill = {1,2,3};
        RingBuffer instance = new RingBuffer(prefill, 0, 3);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 3);
        assertEquals(3, result);
        assertEquals("[1, 2, 3]", Arrays.toString(buffer));
        assertEquals(0, instance.getTail());
        assertEquals(0, instance.getLength());
    }
    @Test public void testRead4() {
        System.out.println("read(byte[],int,int)  [underrun non-wrapping]");
        byte[] prefill = {1,2,0};
        RingBuffer instance = new RingBuffer(prefill, 0, 2);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 3);
        assertEquals(2, result);
        assertEquals("[1, 2, 0]", Arrays.toString(buffer));
        assertEquals(2, instance.getTail());
        assertEquals(0, instance.getLength());
    }
    @Test public void testRead5() {
        System.out.println("read(byte[],int,int)  [offset wrapping]");
        byte[] prefill = {2,3,1};
        RingBuffer instance = new RingBuffer(prefill, 2, 3);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 2);
        assertEquals(2, result);
        assertEquals("[1, 2, 0]", Arrays.toString(buffer));
        assertEquals(1, instance.getTail());
        assertEquals(1, instance.getLength());
    }
    @Test public void testRead6() {
        System.out.println("read(byte[],int,int)  [full wrapping]");
        byte[] prefill = {2,3,1};
        RingBuffer instance = new RingBuffer(prefill, 2, 3);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 3);
        assertEquals(3, result);
        assertEquals("[1, 2, 3]", Arrays.toString(buffer));
        assertEquals(2, instance.getTail());
        assertEquals(0, instance.getLength());
    }
    @Test public void testRead7() {
        System.out.println("read(byte[],int,int)  [underrun wrapping]");
        byte[] prefill = {2,3,1};
        RingBuffer instance = new RingBuffer(prefill, 2, 3);
        byte[] buffer = new byte[4];
        int result = instance.read(buffer, 0, 4);
        assertEquals(3, result);
        assertEquals("[1, 2, 3, 0]", Arrays.toString(buffer));
        assertEquals(2, instance.getTail());
        assertEquals(0, instance.getLength());
    }
    @Test public void testRead8() {
        System.out.println("read(byte[],int,int)  [underrun partly-filled wrapping]");
        byte[] prefill = {2,0,1};
        RingBuffer instance = new RingBuffer(prefill, 2, 2);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 3);
        assertEquals(2, result);
        assertEquals("[1, 2, 0]", Arrays.toString(buffer));
        assertEquals(1, instance.getTail());
        assertEquals(0, instance.getLength());
    }
    @Test public void testRead9() {
        System.out.println("read(byte[],int,int)  [underrun empty]");
        byte[] prefill = {0,0,0};
        RingBuffer instance = new RingBuffer(prefill, 1, 0);
        byte[] buffer = new byte[3];
        int result = instance.read(buffer, 0, 3);
        assertEquals(0, result);
        assertEquals("[0, 0, 0]", Arrays.toString(buffer));
        assertEquals(1, instance.getTail());
        assertEquals(0, instance.getLength());
    }

    @Test public void testGetFreeSpace() {
        System.out.println("getFreeSpace()");
        byte[] prefill = {0,1,0};
        RingBuffer instance = new RingBuffer(prefill, 1, 1);
        assertEquals(2, instance.getFreeSpace());
    }

    @Test public void testGetByteArray() {
        System.out.println("getByteArray()");
        byte[] prefill = {1,2,3};
        RingBuffer instance = new RingBuffer(prefill, 0, 3);
        assertEquals("[1, 2, 3]", Arrays.toString(instance.getByteArray()));
    }
}
