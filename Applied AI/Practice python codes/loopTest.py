# fruitPrices = {'apples': 2.00, 'oranges': 1.50, 'pears': 1.75,
#                'limes': 0.75, 'strawberries': 1.00}


# def buyLotsOfFruit(orderList):
#     """
#         orderList: List of (fruit, numPounds) tuples

#     Returns cost of order
#     """
#     totalCost = 0.0
#     "*** YOUR CODE HERE ***"
#     #check the fruit is present in the list and get the price and calculate the value 
#     #add it to your total cost
#     isError = False
#     for fruit, quantity in orderList:
#         if(fruit in fruitPrices and fruitPrices[fruit]!=None):
#             totalCost += fruitPrices[fruit]*quantity
#         else:
#             isError = True
#             print('The item is not present in the price list')
#             return None
#             break
#     return totalCost


# # Main Method
# if __name__ == '__main__':
#     "This code runs when you invoke the script from the command line"
#     orderList = [('apples', 2.0), ('pears', 3.0), ('limes', 4.0)]
#     print('Cost of', orderList, 'is', buyLotsOfFruit(orderList))


fruitPrices =  {'apples' :1.5, 'oranges':2,'pears':0.75}

OrderList ={'apples':3.0 ,'pears':2.0, 'oranges':4.0,'strawberries':2.0}

#def getTotalCost(OrderList):
totalCost = 0.0
for fruit,quantity in OrderList.items():

    if(fruit in fruitPrices and fruitPrices[fruit]!=None):
          totalCost += fruitPrices[fruit] * quantity
    else:
        print('The '+fruit+' not present in the fruitPrices List')
    
print(totalCost)
      

    #print(fruitPrices[fruit])