(list 
 (cons 'modelSwarm
       (make-instance 'ModelSwarm
                      #:num 20
                      #:vmax 20
                      #:seed 100
		      #:SlStrigger 1
                      #:pslow 0.1
                      #:randomdown 0.1
		      #:troubletrigger 0.0001
		      #:width 200
                      #:history 200)))

