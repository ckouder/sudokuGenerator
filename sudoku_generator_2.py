import random

class NoAvailableSlotException(Exception):
    pass

class Node():
    def __init__(self, _id, value, previous):
        self.id = _id
        self.value = value
        self.previous = previous

class LinkedList():
    def __init__(self):
        self.root = None

    def add(self, _id, value) -> Node:
        node = Node(_id, value, self.root)
        self.root = node

        return node

    def getFromById(self, current: Node, _id: str) -> Node:
        if current == None:
            return None
        
        if current.id == _id:
            return current
        
        return self.getFromById(current.previous, _id)
    
    def getById(self, _id: str) -> Node:
        return self.getFromById(self.root, _id)


class Helper():

    @staticmethod
    def shuffle(a: list):
        """randomize token list"""
        for i in range(0, len(a)):
            j: int = random.randint(i, len(a) - 1)
            temp = a[i]
            a[i] = a[j]
            a[j] = temp
        return a


class Sudoku():
    """Create a new sudoku class"""

    def __init__(self, block_size: int):
        """Initialize a sudoku"""

        if (block_size > 16):
            raise Exception('the block size is too big')

        # size of a block
        self.block_size = block_size

        # size of sudoku puzzle
        self.size = block_size ** 2

        # get tokens from random list
        self.tokens = [str(chr(i)) for i in range(65, 65 + self.size)]

        # set limit for trials
        self.INITIALIZATION_LIMIT = 10000
        self.initialize()

    def initialize(self):
        """initialize the sudoku"""
        self.counter = 0
        self.paper = [[None for i in range(0, self.size)] for j in range(0, self.size)]
        self.pos_tokens_map = LinkedList()
        self.fillDiagWithRandomTokens()
        self.fillAll()

    def fillAll(self):
        """fill all sudoku blanks"""
        col, row = self.getNextSlot(0, 0)

        while True:
            try:
                if self.fillOne(col, row):
                    col, row = self.getNextSlot(col, row)

                else:
                    col, row = self.getPreviousSlot(col, row)

                self.counter += 1

                if self.counter >= self.INITIALIZATION_LIMIT:
                    self.initialize()
                    return

            except NoAvailableSlotException:
                break

    def fillDiagWithRandomTokens(self):
        """fill up a diagonal using token"""
        for j in range(0, self.block_size):
            tokens = Helper.shuffle(self.tokens.copy())
            self.setBlock(tokens, j, j)


    def fillOne(self, column: int, row: int) -> bool:
        """fill up a slot using an available token"""
        tokens = self.getAvailableTokensForSlot(column, row)

        # print(column, row)

        if len(tokens.value) == 0:
            # print("no token found, should return to previous slot")
            # print(self.pos_tokens_map.root.id, self.pos_tokens_map.root.previous.id)
            self.pos_tokens_map.root = self.pos_tokens_map.root.previous
            self.paper[row][column] = None
            return False
        
        else:
            # print("tokens found, fill up")
            # randomly pick up a token to fill the slot
            i = random.randint(0, len(tokens.value) - 1)
            self.paper[row][column] = tokens.value[i]

            # remove the token from list so that we can exempt it when back
            # to the slot again
            tokens.value.remove(tokens.value[i])
            return True

    def getPreviousSlot(self, column: int, row: int):
        """get previous filled slot"""
        if column == self.block_size and row == 0:
            raise NoAvailableSlotException("no available slot", column, row)

        while True:
            if column == 0 and row > 0:
                # print("move to previous line \n")
                column = self.size - 1
                row -= 1

            elif row >= 0:
                column -= 1
                
            if not self.posInDiagonal(column, row):
                break
        
        return {
            column: column,
            row: row
        }

    def getNextSlot(self, column: int, row: int):
        """get next empty slot for filling"""
        if row == self.size - 1 and column == self.size - self.block_size - 1:
            raise NoAvailableSlotException("no available slot")

        while True:
            if column == self.size - 1 and row < self.size - 1:
                # print("move to next line \n")
                column = 0
                row += 1

            elif row <= self.size - 1:
                column += 1
            
            if not self.posInDiagonal(column, row):
                break
        
        return {
            column: column,
            row: row
        }        

    def posInDiagonal(self, column: int, row: int) -> bool:
        """check if given position is in diagonal blocks"""
        for i in range(0, self.block_size):
            if row in range(self.block_size * i, self.block_size * (i + 1)) \
                and column in range(self.block_size * i, self.block_size * (i + 1)):
                return True

        return False


    def getAvailableTokensForSlot(self, column: int, row: int, fresh: bool = False) -> list:
        """get available tokens for a slot"""
        tokens = self.pos_tokens_map.getById('{} {}'.format(column, row))

        # print("tokens length: ", len(tokens.value) if tokens else "Not exist")
        if fresh or tokens == None:
            occupied_tokens = self.getBlock( \
                    math.floor(column / self.block_size), \
                    math.floor(row / self.block_size) \
                )

            occupied_tokens.extend(self.getLine(column, True))
            occupied_tokens.extend(self.getLine(row))
            token_values = self.tokens.copy()

            # remove all values that occur in either horizontal lines, vertical lines
            # or blocks
            for occupied_token in occupied_tokens:
                try:
                    token_values.remove(occupied_token)
                except ValueError:
                    continue

            if tokens == None:
                tokens = self.pos_tokens_map.add('{} {}'.format(column, row), token_values)

            else:
                tokens.value = token_values
        
        # print("[{}] available tokens: ".format(self.getAvailableTokensForSlot.__name__), tokens.value)
        return tokens


    def getLine(self, pos: int, vertical: bool = False) -> list:
        """get line based on position"""
        line: list = []
        if vertical:
            for l in self.paper:
                line.append(l[pos])
        else:
            line = self.paper[pos]

        return line

# ---------------------------------------------------------------------------- #
#                               get or set block                               #
# ---------------------------------------------------------------------------- #

    def getBlock(self, column: int, row: int) -> list:
        """get a sudoku block with column and row number"""
        if column >= self.block_size or row >= self.block_size:
            raise Exception("coordinate surpasses block size")
        
        block: list = []
        for l in self.paper[row * self.block_size : (row + 1) * self.block_size]:
            block.extend(l[column * self.block_size : (column + 1) * self.block_size])
        
        return block

    
    def setBlock(self, values: list, column: int, row: int):
        """fill a sudoku block with values"""
        if column >= self.block_size or row >= self.block_size:
            raise Exception("coordinate surpasses block size")

        counter: int = 0
        for l in self.paper[row * self.block_size : (row + 1) * self.block_size]:
            l[column * self.block_size : (column + 1) * self.block_size] \
                = values[counter * self.block_size : (counter + 1) * self.block_size]
            counter += 1


sudoku_test: Sudoku = Sudoku(4)

for l in sudoku_test.paper:
    print(l)

print("invoke fillOne for {} times".format(sudoku_test.counter))