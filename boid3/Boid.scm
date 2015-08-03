(list 
 (cons 'modelSwarm
       (make-instance 'ModelSwarm
		      #:worldXSize 150
		      #:worldYSize 150
		      #:bugDensity 0.005
		      #:defaultSpeed 2.0
		      #:maxSpeed 4.0
		      #:minSpeed 2.0
		      #:accel 1.05
		      #:optDistance 5.0
		      #:searchSpace 10
		      #:gravityWeight 0.05
		      #:nearWeight 0.15
		      )))

