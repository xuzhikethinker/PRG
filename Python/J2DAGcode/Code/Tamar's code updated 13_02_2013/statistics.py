

def list_stats(input_list):
    """Takes a list as an argument, and returns a list of various statistics e.g. average of the values in the list"""
    from math import sqrt
    list_sum = sum(input_list)
    list_average = list_sum/(float(len(input_list)))
    square_diff = 0.0
    for value in input_list:
        diff = value - list_average
        diff_squared = diff**2
        square_diff += diff_squared
    std_dev = sqrt(square_diff/(float(len(input_list))-1.0))
    std_error = std_dev/sqrt(float(len(input_list)))
    return[std_error, std_dev, list_sum, list_average]


def clean_list_of_None(dirty_list):
    """This method takes a list as an argument, removes any items with value=None, and returns the "cleaned" list"""
    def is_true(x):
        if x!=None:
            return 1
        else: return 0
    clean_list = filter(is_true, dirty_list)
    return clean_list


def combination(n,k):
    """Returns the combination n choose k"""
    from math import factorial
    n_choose_k = float(factorial(n))/float(factorial(k)*factorial(n-k))
    return n_choose_k