(list 
 (cons 'modelSwarm
       (make-instance 'ModelSwarm
                      #:N 5000
                      #:male_femaleRatio 0.5
                      #:tGeneRatio 0.5
                      #:pGeneRatio 0.5
                      #:maleDeathProb 0.0
                      #:sRatio 0.2
                      #:femaleDeathProb 0.0
                      #:a0Coeff 2.0
                      #:a1Coeff 3.0
                      #:EndTime 500
                      #:lifetime 1
                      #:numChild 2)))

