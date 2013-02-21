package com.awirtz.util;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class RingBufferTest {

    public RingBufferTest() {}
    
    @Test public void testRingBuffer() {
        byte[] buffer = {1, 2, 3};
        assertEquals("new RingBuffer(int)",
                "[0, 0, 0], 0, 0", new RingBuffer(3).toString());
        assertEquals("new RingBuffer(byte[])",
                "[1, 2, 3], 0, 0", new RingBuffer(buffer).toString());
        assertEquals("new RingBuffer(byte[],int,int)",
                "[1, 2, 3], 1, 2", new RingBuffer(buffer, 1, 2).toString());
    }
    
    @Test public void testGetTail() {
        byte[] buffer = {1, 2, 3};
        assertEquals("getTail() [empty]",
                0, new RingBuffer(buffer, 0, 0).getTail());
        assertEquals("getTail() [offset empty]",
                1, new RingBuffer(buffer, 1, 0).getTail());
        assertEquals("getTail() [part-full]",
                0, new RingBuffer(buffer, 0, 1).getTail());
        assertEquals("getTail() [offset part-full]",
                1, new RingBuffer(buffer, 1, 1).getTail());
        assertEquals("getTail() [full]",
                0, new RingBuffer(buffer, 0, 3).getTail());
        assertEquals("getTail() [offset full]",
                1, new RingBuffer(buffer, 1, 3).getTail());
    }
    
    @Test public void testGetLength() {
        byte[] buffer = {1, 2, 3};
        assertEquals("getLength() [empty]",
                0, new RingBuffer(buffer, 0, 0).getLength());
        assertEquals("getLength() [offset empty]",
                0, new RingBuffer(buffer, 1, 0).getLength());
        assertEquals("getLength() [part-full]",
                1, new RingBuffer(buffer, 0, 1).getLength());
        assertEquals("getLength() [offset part-full]",
                1, new RingBuffer(buffer, 1, 1).getLength());
        assertEquals("getLength() [full]",
                3, new RingBuffer(buffer, 0, 3).getLength());
        assertEquals("getLength() [offset full]",
                3, new RingBuffer(buffer, 1, 3).getLength());
    }
    
    @Test public void testGetFreeSpace() {
        byte[] buffer = {1, 2, 3};
        assertEquals("getFreeSpace() [empty]",
                3, new RingBuffer(buffer, 0, 0).getFreeSpace());
        assertEquals("getFreeSpace() [offset empty]",
                3, new RingBuffer(buffer, 1, 0).getFreeSpace());
        assertEquals("getFreeSpace() [part-full]",
                2, new RingBuffer(buffer, 0, 1).getFreeSpace());
        assertEquals("getFreeSpace() [offset part-full]",
                2, new RingBuffer(buffer, 1, 1).getFreeSpace());
        assertEquals("getFreeSpace() [full]",
                0, new RingBuffer(buffer, 0, 3).getFreeSpace());
        assertEquals("getFreeSpace() [offset full]",
                0, new RingBuffer(buffer, 1, 3).getFreeSpace());
    }

    @Test public void testGetByteArray() {
        System.out.println("getByteArray()");
        byte[] prefill = {1,2,3};
        RingBuffer instance = new RingBuffer(prefill, 0, 3);
        assertEquals("[1, 2, 3]", Arrays.toString(instance.getByteArray()));
    }
    
    private String testWriteHelper(int tail, int length, int offset, int n) {
        byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {6,7,8};
        RingBuffer instance = new RingBuffer(prefill, tail, length);
        int result = instance.write(buffer, offset, n);
        return result + ":" + instance;
    }
    @Test public void testWrite() {
        assertEquals("write(byte[],int,int)  [non-wrapping]",
                "2:[2, 3, 8], 0, 2", testWriteHelper(0, 0, 1, 2));
        assertEquals("write(byte[],int,int)  [offset non-wrapping]",
                "2:[6, 2, 3], 1, 2", testWriteHelper(1, 0, 1, 2));
        assertEquals("write(byte[],int,int)  [full non-wrapping]",
                "3:[2, 3, 4], 0, 3", testWriteHelper(0, 0, 1, 3));
        assertEquals("write(byte[],int,int)  [over-full non-wrapping]",
                "3:[2, 3, 4], 0, 3", testWriteHelper(0, 0, 1, 4));
        assertEquals("write(byte[],int,int)  [offset wrapping]",
                "2:[3, 7, 2], 2, 2", testWriteHelper(2, 0, 1, 2));
        assertEquals("write(byte[],int,int)  [offset wrapping]",
                "3:[3, 4, 2], 2, 3", testWriteHelper(2, 0, 1, 3));
        assertEquals("write(byte[],int,int)  [overrun wrapping]",
                "3:[3, 4, 2], 2, 3", testWriteHelper(2, 0, 1, 4));
        assertEquals("write(byte[],int,int)  [prefilled wrapping]",
                "2:[3, 7, 2], 1, 3", testWriteHelper(1, 1, 1, 2));
        assertEquals("write(byte[],int,int)  [prefilled overrun wrapping]",
                "2:[3, 7, 2], 1, 3", testWriteHelper(1, 1, 1, 3));
        assertEquals("write(byte[],int,int)  [prefilled full]",
                "0:[6, 7, 8], 1, 3", testWriteHelper(1, 3, 1, 3));
        boolean oob = false; try { testWriteHelper(2, 0, 3, 3); }
        catch (IndexOutOfBoundsException e) { oob = true; }
        assertTrue("write(byte[],int,int)  [out-of-bounds]", oob);
    }

    private String testWriterHelper(int tail, int length, final int offset, int n) {
        final byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {6,7,8};
        RingBuffer instance = new RingBuffer(prefill, tail, length);
        int result = instance.writer(new RingBufferWriter() {
            int ptr = offset;
            @Override public void write(byte[] buf, int off, int len) {
                System.arraycopy(buffer, ptr, buf, off, len);
                ptr += len;
            }
        }, n);
        return result + ":" + instance;
    }
    @Test public void testWriter() {
        assertEquals("writer(byte[],int,int)  [non-wrapping]",
                "2:[2, 3, 8], 0, 2", testWriterHelper(0, 0, 1, 2));
        assertEquals("writer(byte[],int,int)  [offset non-wrapping]",
                "2:[6, 2, 3], 1, 2", testWriterHelper(1, 0, 1, 2));
        assertEquals("writer(byte[],int,int)  [full non-wrapping]",
                "3:[2, 3, 4], 0, 3", testWriterHelper(0, 0, 1, 3));
        assertEquals("writer(byte[],int,int)  [overrun non-wrapping]",
                "3:[2, 3, 4], 0, 3", testWriterHelper(0, 0, 1, 4));
        assertEquals("writer(byte[],int,int)  [offset wrapping]",
                "2:[3, 7, 2], 2, 2", testWriterHelper(2, 0, 1, 2));
        assertEquals("writer(byte[],int,int)  [offset wrapping]",
                "3:[3, 4, 2], 2, 3", testWriterHelper(2, 0, 1, 3));
        assertEquals("writer(byte[],int,int)  [overrun wrapping]",
                "3:[3, 4, 2], 2, 3", testWriterHelper(2, 0, 1, 4));
        assertEquals("writer(byte[],int,int)  [prefilled wrapping]",
                "2:[3, 7, 2], 1, 3", testWriterHelper(1, 1, 1, 2));
        assertEquals("writer(byte[],int,int)  [prefilled overrun wrapping]",
                "2:[3, 7, 2], 1, 3", testWriterHelper(1, 1, 1, 3));
        assertEquals("writer(byte[],int,int)  [prefilled full]",
                "0:[6, 7, 8], 1, 3", testWriterHelper(1, 3, 1, 3));
    }

    private String testReadHelper(int tail, int length, int offset, int n) {
        byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {6,7,8};
        RingBuffer instance = new RingBuffer(prefill, tail, length);
        int result = instance.read(buffer, offset, n);
        return result + ":" + Arrays.toString(buffer) + ", " + instance.getTail() + ", " + instance.getLength();
    }
    @Test public void testRead() {
        assertEquals("read(byte[],int,int)  [non-wrapping]",
                "2:[1, 6, 7, 4, 5], 2, 1", testReadHelper(0, 3, 1, 2));
        assertEquals("read(byte[],int,int)  [offset non-wrapping]",
                "2:[1, 7, 8, 4, 5], 0, 1", testReadHelper(1, 3, 1, 2));
        assertEquals("read(byte[],int,int)  [full non-wrapping]",
                "3:[1, 6, 7, 8, 5], 0, 0", testReadHelper(0, 3, 1, 3));
        assertEquals("read(byte[],int,int)  [underrun non-wrapping]",
                "2:[1, 6, 7, 4, 5], 2, 0", testReadHelper(0, 2, 1, 3));
        assertEquals("read(byte[],int,int)  [offset wrapping]",
                "2:[1, 8, 6, 4, 5], 1, 1", testReadHelper(2, 3, 1, 2));
        assertEquals("read(byte[],int,int)  [full wrapping]",
                "3:[1, 8, 6, 7, 5], 2, 0", testReadHelper(2, 3, 1, 3));
        assertEquals("read(byte[],int,int)  [underrun wrapping]",
                "3:[1, 8, 6, 7, 5], 2, 0", testReadHelper(2, 3, 1, 4));
        assertEquals("read(byte[],int,int)  [underrun partly-filled wrapping]",
                "2:[1, 8, 6, 4, 5], 1, 0", testReadHelper(2, 2, 1, 3));
        assertEquals("read(byte[],int,int)  [underrun empty]",
                "0:[1, 2, 3, 4, 5], 1, 0", testReadHelper(1, 0, 1, 3));
        assertEquals("read(byte[],int,int)  [underrun empty]",
                "0:[1, 2, 3, 4, 5], 1, 0", testReadHelper(1, 0, 1, 3));
        boolean oob = false; try { testReadHelper(1, 3, 3, 3); }
        catch (IndexOutOfBoundsException e) { oob = true; }
        assertTrue("read(byte[],int,int)  [out-of-bounds]", oob);
    }

    private String testReaderHelper(int tail, int length, final int offset, int n) {
        final byte[] buffer = {1,2,3,4,5};
        byte[] prefill = {6,7,8};
        RingBuffer instance = new RingBuffer(prefill, tail, length);
        int result = instance.reader(new RingBufferReader() {
            int ptr = offset;
            @Override public void read(byte[] buf, int off, int len) {
                System.arraycopy(buf, off, buffer, ptr, len);
                ptr += len;
            }
        }, n);
        return result + ":" + Arrays.toString(buffer) + ", " + instance.getTail() + ", " + instance.getLength();
    }
    @Test public void testReader() {
        assertEquals("reader(byte[],int,int)  [non-wrapping]",
                "2:[1, 6, 7, 4, 5], 2, 1", testReaderHelper(0, 3, 1, 2));
        assertEquals("reader(byte[],int,int)  [offset non-wrapping]",
                "2:[1, 7, 8, 4, 5], 0, 1", testReaderHelper(1, 3, 1, 2));
        assertEquals("reader(byte[],int,int)  [full non-wrapping]",
                "3:[1, 6, 7, 8, 5], 0, 0", testReaderHelper(0, 3, 1, 3));
        assertEquals("reader(byte[],int,int)  [underrun non-wrapping]",
                "2:[1, 6, 7, 4, 5], 2, 0", testReaderHelper(0, 2, 1, 3));
        assertEquals("reader(byte[],int,int)  [offset wrapping]",
                "2:[1, 8, 6, 4, 5], 1, 1", testReaderHelper(2, 3, 1, 2));
        assertEquals("reader(byte[],int,int)  [full wrapping]",
                "3:[1, 8, 6, 7, 5], 2, 0", testReaderHelper(2, 3, 1, 3));
        assertEquals("reader(byte[],int,int)  [underrun wrapping]",
                "3:[1, 8, 6, 7, 5], 2, 0", testReaderHelper(2, 3, 1, 4));
        assertEquals("reader(byte[],int,int)  [underrun partly-filled wrapping]",
                "2:[1, 8, 6, 4, 5], 1, 0", testReaderHelper(2, 2, 1, 3));
        assertEquals("reader(byte[],int,int)  [underrun empty]",
                "0:[1, 2, 3, 4, 5], 1, 0", testReaderHelper(1, 0, 1, 3));
        assertEquals("reader(byte[],int,int)  [underrun empty]",
                "0:[1, 2, 3, 4, 5], 1, 0", testReaderHelper(1, 0, 1, 3));
    }
}
