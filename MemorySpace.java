import java.util.ArrayList;
import java.util.List;


/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {
	
	// A list of the memory blocks that are presently allocated
	private LinkedList allocatedList;

	// A list of memory blocks that are presently free
	private LinkedList freeList;

	/**
	 * Constructs a new managed memory space of a given maximal size.
	 * 
	 * @param maxSize
	 *            the size of the memory space to be managed
	 */
	public MemorySpace(int maxSize) {
		// initiallizes an empty list of allocated blocks.
		allocatedList = new LinkedList();
	    // Initializes a free list containing a single block which represents
	    // the entire memory. The base address of this single initial block is
	    // zero, and its length is the given memory size.
		freeList = new LinkedList();
		freeList.addLast(new MemoryBlock(0, maxSize));
	}

	/**
	 * Allocates a memory block of a requested length (in words). Returns the
	 * base address of the allocated block, or -1 if unable to allocate.
	 * 
	 * This implementation scans the freeList, looking for the first free memory block 
	 * whose length equals at least the given length. If such a block is found, the method 
	 * performs the following operations:
	 * 
	 * (1) A new memory block is constructed. The base address of the new block is set to
	 * the base address of the found free block. The length of the new block is set to the value 
	 * of the method's length parameter.
	 * 
	 * (2) The new memory block is appended to the end of the allocatedList.
	 * 
	 * (3) The base address and the length of the found free block are updated, to reflect the allocation.
	 * For example, suppose that the requested block length is 17, and suppose that the base
	 * address and length of the the found free block are 250 and 20, respectively.
	 * In such a case, the base address and length of of the allocated block
	 * are set to 250 and 17, respectively, and the base address and length
	 * of the found free block are set to 267 and 3, respectively.
	 * 
	 * (4) The new memory block is returned.
	 * 
	 * If the length of the found block is exactly the same as the requested length, 
	 * then the found block is removed from the freeList and appended to the allocatedList.
	 * 
	 * @param length
	 *        the length (in words) of the memory block that has to be allocated
	 * @return the base address of the allocated block, or -1 if unable to allocate
	 */
	public int malloc(int length) {		
		if (length <= 0) {
			throw new IllegalArgumentException("Block length must be positive");
		}
	
		ListIterator itr = freeList.iterator();
		MemoryBlock blockToAllocate = null;
	
		while (itr.hasNext()) {
			MemoryBlock freeBlock = itr.next();
			if (freeBlock.length >= length) {
				blockToAllocate = new MemoryBlock(freeBlock.baseAddress, length);
	
				if (freeBlock.length == length) {
					freeList.remove(freeBlock);
				} else {
					freeBlock.baseAddress += length;
					freeBlock.length -= length;
				}
				break;
			}
		}
		if (blockToAllocate == null) {
			return -1; 
		}
		allocatedList.addLast(blockToAllocate);
		return blockToAllocate.baseAddress;
	}

	/**
	 * Frees the memory block whose base address equals the given address.
	 * This implementation deletes the block whose base address equals the given 
	 * address from the allocatedList, and adds it at the end of the free list. 
	 * 
	 * @param baseAddress
	 *            the starting address of the block to freeList
	 */
	public void free(int address) {
		if (allocatedList.getSize() == 0) {
			throw new IllegalArgumentException("index must be between 0 and size");
		}
		ListIterator freeItr = freeList.iterator();
        while (freeItr.hasNext()) {
            MemoryBlock block = freeItr.next();
            if (block.baseAddress == address) {
                return; 
            }
        }
        ListIterator allocItr = allocatedList.iterator();
        MemoryBlock blockToFree = null;
        
        while (allocItr.hasNext()) {
            MemoryBlock block = allocItr.next();
            if (block.baseAddress == address) {
                blockToFree = block;
                break;
            }
        }
        if (blockToFree != null) {
            allocatedList.remove(blockToFree);
            freeList.addLast(blockToFree);
        }
	}

	
	/**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
	public String toString() {
		return freeList.toString() + "\n" + allocatedList.toString();		
	}
	
	/**
	 * Performs defragmantation of this memory space.
	 * Normally, called by malloc, when it fails to find a memory block of the requested size.
	 * In this implementation Malloc does not call defrag.
	 */
	public void defrag() {
		/// TODO: Implement defrag test
		//// Write your code here
		if (freeList.getSize() <= 1) return;
        sortFreeList();
        ListIterator iter = freeList.iterator();
        while (iter.hasNext()) {
            MemoryBlock currentBlock = iter.next();
            if (!iter.hasNext()) break;
            ListIterator innerIter = freeList.iterator();
            MemoryBlock nextBlock = null;
            while (innerIter.hasNext() && innerIter.next() != currentBlock);
            
            if (innerIter.hasNext()) {
                nextBlock = innerIter.next();
            }
            if (nextBlock != null && currentBlock.baseAddress + currentBlock.length == nextBlock.baseAddress) {
                currentBlock.length += nextBlock.length;
                freeList.remove(nextBlock);
                iter = freeList.iterator();
            }
        }
    }
    
    private void sortFreeList() {
        if (freeList.getSize() <= 1) return;
    
        boolean swapped;
        do {
            swapped = false;
            Node current = freeList.getNode(0);
            while (current != null && current.next != null) {
                if (current.block.baseAddress > current.next.block.baseAddress) {
                    MemoryBlock temp = current.block;
                    current.block = current.next.block;
                    current.next.block = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }
}
//My Tests:


class MemorySpaceTest1234 {
    public static void main(String[] args) {
        // defragTest1();
        // defragTest2();
        // defragTest3();
        // defragTest4();
		// testDefrag();
    }

    // Test 1: Defrag with an empty freeList
	public static void defragTest1() {
        MemorySpace memorySpace = new MemorySpace(100);
        memorySpace.defrag();
        String expected = "(0 , 100)\n";
        String actual = memorySpace.toString().split("\n")[0];
        System.out.println("Test 1: " + (expected.equals(actual) ? "Passed" : "Failed"));
    }

    public static void defragTest2() {
        MemorySpace memorySpace = new MemorySpace(100);
        memorySpace.malloc(50);
        memorySpace.defrag();
        String expected = "(50 , 50)\n";
        String actual = memorySpace.toString().split("\n")[0];
        System.out.println("Test 2: " + (expected.equals(actual) ? "Passed" : "Failed"));
    }

    public static void defragTest3() {
        MemorySpace memorySpace = new MemorySpace(100);
        int addr1 = memorySpace.malloc(20);
        int addr2 = memorySpace.malloc(30);
        memorySpace.free(addr1);
        memorySpace.free(addr2);
        memorySpace.defrag();
        String expected = "(0 , 50)\n";
        String actual = memorySpace.toString().split("\n")[0];
        System.out.println("Test 3: " + (expected.equals(actual) ? "Passed" : "Failed"));
    }

    public static void defragTest4() {
        MemorySpace memorySpace = new MemorySpace(100);
        int addr1 = memorySpace.malloc(20);
        int addr2 = memorySpace.malloc(30);
        int addr3 = memorySpace.malloc(10);
        memorySpace.free(addr1);
        memorySpace.free(addr2);
        memorySpace.free(addr3);
        memorySpace.defrag();
        String expected = "(0 , 60)\n";
        String actual = memorySpace.toString().split("\n")[0];
        System.out.println("Test 4: " + (expected.equals(actual) ? "Passed" : "Failed"));
    }
    
}


