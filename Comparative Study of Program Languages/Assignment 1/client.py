'''
Created on May 17, 2020

@author: Vekesh
'''
#STEPS TO CREATE THE CLIENT 
#CREATE CLIENT CONNECTION - USING SOCKETS
#!/usr/bin/env python3

import socket

#Creating client socket connection
s= socket.socket()
port = 9999
s.connect(('127.0.0.1',port))

#Function to send and receive information from the server
def send_receive_server(data,method):
    tempData=(str(data+"|"+method))   
    s.sendall(tempData.encode('utf-8')) 
    receivedData = s.recv(4096)
    print(receivedData.decode())

#Function for printing the data in the dictionary    
def print_sorted_report(method):
    s.sendall(method.encode('utf-8')) 
    receivedData = s.recv(4096)
    print(receivedData.decode())

#function for sending the name of customer and method to the server  inorder to receive the result
def find_customer(name):
    tempData=(str(name+",findCustomer"))   
    s.sendall(tempData.encode('utf-8')) 
    receivedData = s.recv(4096)
    print(receivedData.decode())

    
while True:

    print('1. Find customer')
    print('2. Add customer')
    print('3. Delete customer')
    print('4. Update customer age')
    print('5. Update customer address')
    print('6. Update customer phone')
    print('7. Print report')
    print('8. Exit')
    inp = int(input("Select: "))
    if inp == 1:
        name = str(input("Name:"))
        send_receive_server(name,"findCustomer")  
                          
    elif inp == 2:
        name = str(input("Name:"))
        age =  str(input("Age:"))
        address = str(input("Address:"))
        phone = str(input("Phone:"))
        tempData = name+","+str(age)+","+address+","+phone
        send_receive_server(tempData,"addCustomer")
        
    elif inp == 3:
        name = str(input("Name:"))
        send_receive_server(name,"deleteCustomer")
        
    elif inp == 4:
        name = str(input("Name:"))
        age =  int(input("Age:"))
        tempData = name+","+str(age)
        send_receive_server(tempData,"updateAge")
        
    elif inp == 5:
        name = str(input("Name:"))
        address = str(input("Address:"))
        tempData = name+","+address
        send_receive_server(tempData,"updateAddress")
        
    elif inp == 6:
        name = str(input("Name:"))
        phone = str(input("Phone:"))
        tempData = name+","+phone
        send_receive_server(tempData,"updatePhone")
        
    elif inp == 7:                        
        print_sorted_report("printReport")

    elif inp == 8:            
        print('Good bye')
        send_receive_server("","Exit")
        break
    else:
        print("Invalid input!")
    
s.close()
