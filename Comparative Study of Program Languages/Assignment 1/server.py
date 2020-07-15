'''
Created on May 17, 2020

@author: Vekesh
'''
#STEPS TO CREATE THE SERVER 
#CREATE SERVER CONNECTION - USING SOCKETS
#!/usr/bin/env python3

import socket

#Establishing the Server Socket Connection
s = socket.socket()
print (" Socket create successfully.")
port = 9999
s.bind(('127.0.0.1',port))
print("Socket is binded to %s" %(port))
s.listen(5)
print("Socket is listening")

#Server is always on and running
# Load the text file into the memory
f = open("data.txt","r");
data ={}

#Function to validate the age 
def validate_age(ageInfo):   
    if not ageInfo or ageInfo==' ' or (ageInfo.isdigit() and int(ageInfo) > 0):
        return True    
    else:
        return False
with open("data.txt") as info:
    lines = info.readlines()    
    for eachLine in lines:
        eachData = tuple(eachLine.split("|"))       #split each values , remove the whitespaces and validate the names  
        if (eachData[0]!='' and eachData[0]!=' ' and validate_age(eachData[1].strip())):          
            data[eachData[0].strip().capitalize()]={ "name":eachData[0].strip().capitalize(), "age":eachData[1].strip(),"address":eachData[2].strip(),"phone":eachData[3].strip()}
    
#function to find the customer in the memory
def check_customer(name):
    if name in data.keys():
        sortedData=''
        dataInfo = data[name]
        for innerData in dataInfo:
            sortedData+= " "+innerData +"-"+dataInfo[innerData]+";"
        sortedData+="\n"        
        return sortedData
    else: 
        return "Customer Not found"

#function to add the customer in to the dictionary    
def add_customer(txtInfo):
    #split the info
    txtData = tuple(txtInfo.split(",")) 
    if txtData[0] in data.keys():
        return 'Customer already exists'
    else:
        if validate_age(txtData[1].strip()):            
            data[txtData[0].capitalize()]={ "name":txtData[0].strip().capitalize(), "age":txtData[1].strip(),"address":txtData[2].strip(),"phone":txtData[3].strip()}
            return 'Customer has been added successfully'   
        else:
            return'Customer details are invalid' 

#function to delete the customer   
def delete_customer(name):
    if name in data.keys():
        del data[name]
        return "Customer has been deleted successfully"
    else: 
        return "Customer  does not exists"

#function to update the age of the given customer    
def update_age(name,age):
    if validate_age(age):  
        age_c = int(age)
        if name in data.keys():        
            if ((type(age_c)== int) and age_c.__bool__()) :
                data[name]['age'] = age_c
                return "Customer age has been update successfully"
            else:
                return "Given age input is not an number"
        else :
            return "Customer not found"
    else:
        return "Not an valid age given"

#function to update the address of the customer
def update_address(name,addressValue):
    if name in data.keys():        
        if ((type(addressValue) == str) and (addressValue!='')):
            data[name]['address']= addressValue
            return "Customer address updated"
        else:
            return "Given age input is not an number"
    else :
        return "Customer not found"
    
#function to update the customer's phone number    
def update_phone(name,phone_number):
    if name in data.keys():        
        if ((type(phone_number) == str) and phone_number!='') :
            data[name]['phone'] = phone_number
            return "Customer Phone number updated successfully"
        else:
            return "Given phone number is not valid"
    else :
        return "Customer not found"

#function to sort the data and prin the whole report
def print_report():    
    sortedData =""
    for eachData in sorted(data):     
        dataInfo = data[eachData]
        for innerData in dataInfo:
            sortedData+= " "+str(innerData )+":"+str(dataInfo[innerData])
        sortedData+="\n"
    return str(sortedData);
    
    
while True:
    c,address = s.accept()    
    print('Got connection from address',address)
    while 1:
        tmp = c.recv(4096)
        tempInfo = tmp.decode('utf-8')
        if str(tempInfo).__contains__("|"):
            tempInfo = tempInfo.split("|")
            tempData = tempInfo[0]
            tempMethod = tempInfo[1]
        else:
            tempMethod = tempInfo
            
        #receive the method name and  perform operation based on the method
        if tempMethod=='findCustomer':
            tempres1 = check_customer(tempData)
            res1 = str(tempres1)
            c.sendall(res1.encode('utf-8'))
            
        elif tempMethod=='addCustomer':            
            tempres2 = add_customer(tempData)
            res2 = str(tempres2)
            c.sendall(res2.encode('utf-8'))
            
        elif tempMethod=='deleteCustomer':
            tempres3 = delete_customer(tempData)
            res3 = str(tempres3)
            c.sendall(res3.encode('utf-8'))
            
        elif tempMethod=='updateAge':
            tempData = tempData.split(",")
            tempres4 = update_age(tempData[0],tempData[1])
            res4 = str(tempres4)
            print(res4)
            c.sendall(res4.encode('utf-8'))
            
        elif tempMethod=='updateAddress':
            tempData = tempData.split(",")
            tempres5 = update_address(tempData[0],tempData[1])
            res5 = str(tempres5)
            c.sendall(res5.encode('utf-8'))
            
        elif tempMethod=='updatePhone':
            tempData = tempData.split(",")
            tempres6= update_phone(tempData[0],tempData[1])
            res6 = str(tempres6)
            c.sendall(res6.encode('utf-8'))
            
            
        elif tempMethod=='printReport':            
            tempres7= print_report()
            c.sendall(tempres7.encode('utf-8')) 
            
        elif tempMethod=='Exit':    
            break       
               
    c.close()
    #closing the connection


