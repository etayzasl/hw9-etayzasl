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
	 */
	public int malloc(int length) {
		if (length <= 0) {
			return -1;
		}

		ListIterator iterator = freeList.iterator();
		while (iterator.hasNext()) {
			MemoryBlock freeBlock = iterator.next();
			if (freeBlock.length >= length) {
				int baseAddress = freeBlock.baseAddress;
				
				// Create new allocated block
				MemoryBlock allocatedBlock = new MemoryBlock(baseAddress, length);
				allocatedList.addLast(allocatedBlock);
				
				if (freeBlock.length == length) {
					// If exact size match, remove the free block
					freeList.remove(freeBlock);
				} else {
					// If larger, update the free block
					freeBlock.baseAddress = baseAddress + length;
					freeBlock.length = freeBlock.length - length;
				}
				
				return baseAddress;
			}
		}
		return -1;  // Unable to allocate
	}

	/**
	 * Frees the memory block whose base address equals the given address.
	 */
	public void free(int address) {
		if (allocatedList.getSize() == 0) {
			throw new IllegalArgumentException("index must be between 0 and size");
		}
		
		ListIterator iterator = allocatedList.iterator();
		while (iterator.hasNext()) {
			MemoryBlock block = iterator.next();
			if (block.baseAddress == address) {
				allocatedList.remove(block);
				freeList.addLast(block);
				return;
			}
		}
		throw new IllegalArgumentException("Address not found in allocated blocks");
	}
	
	/**
	 * A textual representation of the free list and the allocated list of this memory space, 
	 * for debugging purposes.
	 */
	public String toString() {
		return freeList.toString() + " \n" + allocatedList.toString() + " ";
	}
	
	/**
	 * Performs defragmentation of this memory space.
	 */
	public void defrag() {
		if (freeList.getSize() <= 1) return;
		
		// Sort free blocks by base address
		// We'll do this by creating a new list and inserting in order
		LinkedList sortedList = new LinkedList();
		ListIterator iterator = freeList.iterator();
		
		while (iterator.hasNext()) {
			MemoryBlock block = iterator.next();
			
			// Find the right position to insert
			int index = 0;
			ListIterator sortedIterator = sortedList.iterator();
			while (sortedIterator.hasNext()) {
				MemoryBlock sortedBlock = sortedIterator.next();
				if (block.baseAddress < sortedBlock.baseAddress) {
					break;
				}
				index++;
			}
			sortedList.add(index, block);
		}
		
		// Replace freeList with sorted list
		freeList = sortedList;
		
		// Now merge adjacent blocks
		iterator = freeList.iterator();
		MemoryBlock prevBlock = iterator.next();
		
		while (iterator.hasNext()) {
			MemoryBlock currentBlock = iterator.next();
			if (prevBlock.baseAddress + prevBlock.length == currentBlock.baseAddress) {
				// Merge blocks
				prevBlock.length = prevBlock.length + currentBlock.length;
				freeList.remove(currentBlock);
				// Start over since we modified the list
				iterator = freeList.iterator();
				prevBlock = iterator.next();
			} else {
				prevBlock = currentBlock;
			}
		}
	}
}

