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
	
    ListIterator iterator = new ListIterator(freeList.getFirst());

    while (iterator.hasNext()) {
		MemoryBlock blockIterator = iterator.current.block;
        if (blockIterator.length >= length) { // If block is large enough
			//MemoryBlock freeBlock = new MemoryBlock(blockIterator.baseAddress, length);
            int Address = blockIterator.baseAddress;
            MemoryBlock allocatedBlock = new MemoryBlock(Address, length);
            
            // Add the allocated block to the allocatedList
            allocatedList.addLast(allocatedBlock);
            // If the free block exactly matches the length, remove it
            if (blockIterator.length == length) {
                freeList.remove(blockIterator);
            } else {
                // Otherwise, split the free block
                blockIterator.baseAddress += length;
                blockIterator.length -=length;
            }
			//System.out.println(length);
            //System.out.println(toString());
            return Address; // Return the base address of the allocated block
        }
        
        iterator.next();
    }
    
    return -1; // Return -1 if no suitable block was found
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
		if (allocatedList.getSize() == 0){
			throw new IllegalArgumentException (
				"index must be between 0 and size"
				);
		}
		// Traverse allocatedList to find the block with the matching base address
        Node current = allocatedList.getFirst();
        while (current != null) {
            MemoryBlock allocatedBlock = current.block;
            if (allocatedBlock.baseAddress == address) {
                // Remove from allocated list
                allocatedList.remove(current);
    
                // Add to freeList
                freeList.addLast(allocatedBlock);
    
                return; // Done freeing the block
            }
            current = current.next; // Move to the next block
        }
    }
	
	/**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
        @Override
	public String toString() {
        return freeList.toString() + "\n" + allocatedList.toString();  
     
    }
	
	/**
	 * Performs defragmantation of this memory space.
	 * Normally, called by malloc, when it fails to find a memory block of the requested size.
	 * In this implementation Malloc does not call defrag.
	 */
	public void defrag() {
		if (freeList.getFirst() == null) return; // Nothing to defragment if the freeList is empty
	
		// Use ListIterator to traverse the freeList
		ListIterator iterator1 = new ListIterator(freeList.getFirst());
	
		while (iterator1.hasNext()) {
			MemoryBlock block1 = iterator1.current.block;
	
			// Use another iterator to compare with all subsequent blocks
			ListIterator iterator2 = new ListIterator(freeList.getFirst());
	
			while (iterator2.hasNext()) {
				MemoryBlock block2 = iterator2.current.block;
	
				if (block1 != block2) { // Skip comparing the block with itself
					// Check if block1 and block2 are adjacent (forward)
					if (block1.baseAddress + block1.length == block2.baseAddress) {
						block1.length += block2.length; // Merge block2 into block1
						freeList.remove(iterator2.current); // Remove block2
						iterator2 = new ListIterator(freeList.getFirst()); // Restart iterator2
					}
					// Check if block2 and block1 are adjacent (backward)
					else if (block2.baseAddress + block2.length == block1.baseAddress) {
						block2.length += block1.length; // Merge block1 into block2
						freeList.remove(iterator1.current); // Remove block1
						iterator1 = new ListIterator(freeList.getFirst()); // Restart iterator1
						break; // Restart the outer loop
					}
				}
	
				iterator2.next(); // Move to the next block in the inner loop
			}
	
			iterator1.next(); // Move to the next block in the outer loop
		}
	
		//System.out.println(toString());
	}
	
}

