(ns salesorder.app (:gen-class)
(:require [clojure.string :as str]))
;load the required files
; Read the data from the files -slurp
; read-line - Get input from the user
;use java only for converting text to numbers for sales calculation
;function for reading the file and split the  content
;definin the required maps or data structures
;basic map
(def customer_map{})
(def product_map{})
(def sales_map{})

;relational maps 
(def total_purchase{})
(def product_total_sales{})
;function for reading txt file
(defn read_from_file [file]
   (sort(str/split-lines(slurp file)))
)
;function to split the content put it into  lists
;load values to a map
(defn load_data_cus [data] 
    (doseq [i data]
    (def eachRow (str/split  i #"\|"))                                           
        (def customer_map (assoc customer_map (Integer/parseInt(get eachRow 0)) {:name (get eachRow 1) :address (get eachRow 2) :phone (get eachRow 3) })) 
    )
)
(defn load_data_prod [data]      
    (doseq [i data]
    (def eachRow (str/split  i #"\|"))                                
        (def product_map (assoc product_map (Integer/parseInt(get eachRow 0)) {:item (get eachRow 1) :cost (Float/parseFloat(get eachRow 2))  }))        
    )
) 
(defn load_data_sales [data] 
    (doseq [i data]
    (def eachRow (str/split  i #"\|"))            
        (def sales_map (assoc sales_map (Integer/parseInt(get eachRow 0)) {:custId (Integer/parseInt(get eachRow 1)) :productId (Integer/parseInt(get eachRow 2)) :count (Integer/parseInt(get eachRow 3)) 
        }))                      
    ) 
)

(load_data_cus (read_from_file "cust.txt"))
(load_data_prod (read_from_file "prod.txt"))
(load_data_sales (read_from_file "sales.txt"))

(defn pre_load_data [data]
    (doseq [[key value] data]                        
        (def customer_data (get customer_map(get value :custId)))        
        (def customer_name (get customer_data :name))
        (def product_data (get product_map(get value :productId)))
        (def product_name  (get product_data :item) )
        (def product_cost  (get product_data :cost))
        (def cost(* product_cost  (get value :count)))
        (def total_cost(+ cost (get total_purchase customer_name 0 )))
        (def total_purchase(assoc total_purchase customer_name total_cost ))
        (def total_product_count (+ (get value :count) (get product_total_sales product_name 0)))
        (def product_total_sales(assoc product_total_sales product_name total_product_count))
    )
)
(pre_load_data sales_map)



;function to display the customer table
(defn show_customer_details []
(sort-by first customer_map)
  (doseq [[key value] customer_map]
    (println key ":[" (get value :name) "," (get value :address) "," (get value :phone) "]")
  ) 
 )
;function to display the product table
(defn show_product_details []
    (doseq [[key value] product_map]
        (println key ":[" (get value :item) "," (get value :cost) "]" )
    )
)
;function to display the product table
(defn show_sales_details []
 ;for sales id: customer id- product id- sale count)
    (doseq [[key value] sales_map]
        (def customer_value (get customer_map(get value :custId)))
        (def product_value (get product_map(get value :productId)))
        (println key ":[" (get customer_value :name) "," (get product_value :item)"," (get value :count) "]")    
    )  
) 
;function to display the total sales  for customer
(defn show_total_sales_customer []
    (println "Key in the customer name:")
    (def customer_name_in (read-line))
    (if (contains? total_purchase customer_name_in) (println (str customer_name_in":"(format "%.2f"(get total_purchase customer_name_in)))) (println "Customer not found"))
    
)
;function to display the total sales for a product
(defn show_total_sales_product []
(println "Key in the  product name:")
 (def prod_name_in (read-line))
 (if (contains? product_total_sales prod_name_in) (println (str prod_name_in":"(get product_total_sales prod_name_in))) (println "Product not found"))
 )

(defn  SalesMenuDisplay []
    (println "*** Sales Menu ***")
    (println "------------------")
    (println "1. Display Customer Table")
    (println "2. Display Product Table")
    (println "3. Display Sales Table")
    (println "4. Total Sales for Customer")
    (println "5. Total Count for Product")
    (println "6. Exit")
    (println "Enter an option?")
    (def option (read-line))
    (case option "1"(show_customer_details)
                 "2" (show_product_details)  
                 "3" (show_sales_details) 
                 "4" (show_total_sales_customer)
                 "5" (show_total_sales_product)              
                 "6" ((println "Good Bye") (System/exit 0))
    )            
    (if (not= option "6")
    (recur))
)
(SalesMenuDisplay)