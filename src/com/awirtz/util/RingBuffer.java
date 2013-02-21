/*
 * Byte-Array Ring Buffer
 */
package com.awirtz.util;

import java.util.Arrays;

/**
 * A ring buffer implementation on top of a byte array.
 * @author Aaron Meriwether
 */
public class RingBuffer {
    private byte[] byteArray;
    private int tail = 0;
    private int length = 0;
    
    /**
     * Create a new RingBuffer of the specified size.
     * @param size The size in bytes of the RingBuffer.
     */
    public RingBuffer(int size) {
        byteArray = new byte[size];
    }

    /**
     * Turn an existing byte array into a RingBuffer.
     * @param buffer A byte array to be used as a RingBuffer.
     */
    public RingBuffer(byte[] buffer) {
        byteArray = buffer;
    }

    /**
     * Turn a byte array which already contains data into a RingBuffer.
     * @param buffer A byte array to be used as a RingBuffer.
     * @param tail The pointer to the beginning of the data in the array.
     * @param length The length of the data in the array.
     */
    public RingBuffer(byte[] buffer, int tail, int length) {
        byteArray = buffer;
        this.tail = tail;
        this.length = length;
    }
    
    /**
     * Write to the RingBuffer from a byte array.
     * If the write exceeds the free space in the RingBuffer, only part of the
     * data will be written.
     * 
     * @param buffer A byte array from which the data will be copied.
     * @param offset The offset in the byte array where the data begins.
     * @param length The number of bytes to be written.
     * @return The number of bytes successfully written to the RingBuffer.
     * This may be less than the requested length if there is insufficient free
     * space in the RingBuffer, or zero if the RingBuffer is full.
     */
    public int write(byte[] buffer, int offset, int length) {
        int head = (this.tail + this.length) % byteArray.length;
        int toEnd = byteArray.length - head;
        // if the request exceeds the free space, write as much as possible
        int toWrite = Math.min(length, byteArray.length - this.length);
        if(toWrite > toEnd) {
            // write from the head to the end
            System.arraycopy(buffer, offset, byteArray, head, toEnd);
            // write the remainder from the beginning
            System.arraycopy(buffer, offset + toEnd, byteArray, 0, toWrite - toEnd);
        } else {
            // write the whole thing at once
            System.arraycopy(buffer, offset, byteArray, head, toWrite);
        }
        // writing increases the length
        this.length += toWrite;
        return toWrite;
    }
    
    /**
     * Perform a write operation via callbacks on a supplied object.
     * This is basically an inversion of the RingBuffer "write" method in which
     * the caller to supplies its own simple "write" method which will be
     * invoked zero, one, or two times against the RingBuffer's internal byte
     * array to complete the operation.
     * 
     * This is useful to avoid an intermediate buffer when reading from a
     * source into a RingBuffer.
     * 
     * @param writer An object implementing the RingBufferWriter interface.
     * @param length The number of bytes to be written.
     * @return The number of bytes successfully written to the RingBuffer.
     * This may be less than the requested length if there is insufficient free
     * space in the RingBuffer, or zero if the RingBuffer is full.
     */
    public int writer(RingBufferWriter writer, int length) {
        int head = (this.tail + this.length) % byteArray.length;
        int toEnd = byteArray.length - head;
        // if the request exceeds the free space, write as much as possible
        int toWrite = Math.min(length, byteArray.length - this.length);
        if(toWrite > toEnd) {
            // write from the head to the end
            writer.write(byteArray, head, toEnd);
            // write the remainder from the beginning
            writer.write(byteArray, 0, toWrite - toEnd);
        } else {
            // write the whole thing at once
            writer.write(byteArray, head, toWrite);
        }
        // writing increases the length
        this.length += toWrite;
        return toWrite;
    }
    
    /**
     * Read from the RingBuffer into a byte array.
     * 
     * @param buffer A byte array in which the read data will be placed.
     * @param offset The offset in the byte array where the read data should be placed.
     * @param length The number of bytes to be read.
     * @return The number of bytes successfully read from the RingBuffer.
     * This may be less than the requested length if there were fewer bytes in
     * the buffer, or zero if the buffer was empty.
     */
    public int read(byte[] buffer, int offset, int length) {
        int toEnd = byteArray.length - this.tail;
        // if the request exceeds the available data, read as much as is available
        int toRead = Math.min(length, this.length);
        if(toRead > toEnd) {
            // read from the tail to the end
            System.arraycopy(byteArray, this.tail, buffer, offset, toEnd);
            // read the requested remainder from the beginning
            System.arraycopy(byteArray, 0, buffer, offset + toEnd, toRead - toEnd);
        } else {
            // read the whole requested thing at once
            System.arraycopy(byteArray, this.tail, buffer, offset, toRead);
        }
        // reading moves the tail and decreases the length
        this.tail = (this.tail + toRead) % byteArray.length;
        this.length -= toRead;
        return toRead;
    }

    /**
     * Perform a read operation via callbacks on a supplied object.
     * This is basically an inversion of the RingBuffer "read" method in which
     * the caller to supplies its own simple "read" method which will be
     * invoked zero, one, or two times against the RingBuffer's internal byte
     * array to complete the operation.
     * 
     * This is useful to avoid an intermediate buffer when reading from a
     * RingBuffer into a destination.
     * 
     * @param reader An object implementing the RingBufferReader interface.
     * @param length The number of bytes to be read.
     * @return The number of bytes successfully read from the RingBuffer.
     * This may be less than the requested length if there were fewer bytes in
     * the buffer, or zero if the buffer was empty.
     */
    public int reader(RingBufferReader reader, int length) {
        int toEnd = byteArray.length - this.tail;
        // if the request exceeds the available data, read as much as is available
        int toRead = Math.min(length, this.length);
        if(toRead > toEnd) {
            // read from the tail to the end
            reader.read(byteArray, this.tail, toEnd);
            // read the requested remainder from the beginning
            reader.read(byteArray, 0, toRead - toEnd);
        } else {
            // read the whole requested thing at once
            reader.read(byteArray, this.tail, toRead);
        }
        // reading moves the tail and decreases the length
        this.tail = (this.tail + toRead) % byteArray.length;
        this.length -= toRead;
        return toRead;
    }
    
    /**
     * Get the length of the data contained in the RingBuffer.
     * @return The length of the data in bytes.
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Get the maximum capacity of the RingBuffer.
     * @return The maximum capacity in bytes.
     */
    public int getMaxLength() {
        return byteArray.length;
    }
    
    /**
     * Get the size of the unused space in the RingBuffer.
     * @return The unused capacity in bytes.
     */
    public int getFreeSpace() {
        return byteArray.length - length;
    }
    
    /**
     * Get the underlying byte array.
     * @return The underlying byte array.
     */
    public byte[] getByteArray() {
        return byteArray;
    }

    /**
     * Get the tail pointer for the underlying byte array.
     * @return The tail pointer.
     */
    public int getTail() {
        return tail;
    }
    
    @Override public String toString() {
        return Arrays.toString(byteArray) + ", " + tail + ", " + length;
    }
}