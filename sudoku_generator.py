import random

class Sudoku():

    def __init__(self, block_size: int):
        self.block_size = block_size
        self.size = block_size ** 2
        self.tokens = [str(chr(i)) for i in range(65, 65 + self.size)]
        self.paper = [[None for i in range(0, self.size)] for j in range(0, self.size)]
    
    def generate_random_token_list(self) -> list:
        tokens: list = self.tokens.copy()

        for i in range(0, len(tokens)):
            j: int = random.randint(i, len(tokens) - 1)
            temp = tokens[i]
            tokens[i] = tokens[j]
            tokens[j] = temp

        return tokens