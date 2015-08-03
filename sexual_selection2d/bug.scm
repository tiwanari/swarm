(list 
 (cons 'modelSwarm
       (make-instance 'ModelSwarm
		      #:randomSeed 5
		      #:worldXSize 80
                      #:worldYSize 80
		      #:t1Rate 0.2
		      #:p1Rate 0.8
		      #:initialBugSum 200
		      #:bugA0 2
		      #:bugA1 3
		      #:extinctProbability 0.2
		      #:visibility 2
		      #:bugSumLimit 200)))

