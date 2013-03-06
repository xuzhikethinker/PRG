# compares n-grams in two text files and outputs a similarity between 0 and 1

text_1 = '../similaritytxt/APEX0104056v3.txt'


import re

word_re = re.compile(r'[A-z\.\,\']+')
plural_re = re.compile('[a-z]+s')
plural_y_re = re.compile('[a-z]+ies')
caps_re = re.compile('[A-Z]')

def compare_texts(A, B):
    source_a = open(A)
    source_b = open(B)
    
    lines_a = source_a.readlines()
    lines_b = source_b.readlines()
    
    for n in range(1, 5):
        #print n
        dict_a = make_dict(lines_a, n)
        dict_b = make_dict(lines_b, n)
        
        print compare_words(dict_a, dict_b)

def compare_words(A, B): # A and B are dictionaries which map strings of words to their frequency in the text
    top = float(dot_product(A, B))
    bottom_sq = float( dot_product(A, A) * dot_product(B, B))
    bottom = bottom_sq**0.5
    value = top/bottom
    return value
    
def dot_product(A, B): # Performs a dot product between the dictionaries treating them as vectors
    value = 0
    for word in A.keys():
        if word in B.keys():
            value += (A[word] * B[word])
    return value        


def make_dict(text, n):
    dict = {}
    last_words = []
    for line in text:
        word_list = get_words(line)
        for word in word_list:
            last_words = list_shift(last_words, word, n+1)
            if len(last_words) == n+1:
                my_tuple = tuple(last_words[:-1])
                following_word = last_words[-1]
                dict = add_dict_entry(dict, my_tuple, following_word)
    return dict
	
def get_words(line):
    # When given a line, returns a list of words on that line
	# Full stops are included
    words = re.findall(word_re, line)
    return words
    	
def list_shift(list, word, n):
    # Adds word to list, and if list is longer than n, drops items from the front until list is length n
    list.append(word)
    while len(list)>n:
        del list[0]
    return list		
	
def add_dict_entry(dict, tuple, word):
    if tuple in dict:
	    # we have seen this word group before!
        dict[tuple] += 1
    else:
        # we have not had this word group before - make a counter for it
        dict[tuple] = 1
    return dict	
    
def cleanup_text(word):
    pass
    
        
    
    
if __name__ == "__main__":
    text_list = ['../similaritytxt/APEX0104056v3.txt', '../similaritytxt/DIFFERENT0210076v1.txt', '../similaritytxt/DIFFERENT0302163v2.txt', '../similaritytxt/DIFFERENT0302176v2.txt', '../similaritytxt/SIMILAR0302097v1.txt', '../similaritytxt/SIMILAR0302155v2.txt', '../similaritytxt/SIMILAR0302174v3.txt']
    for text_2 in text_list:
        print text_2
        compare_texts(text_1, text_2)
    

