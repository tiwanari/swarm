;;----------------------------------------------------------------------------
;; title   : $RCSfile: seg.scm,v $ ($Date: 2005/07/27 17:44:11 $)
;; content : ê›íËÉtÉ@ÉCÉã
;; author  : Ryosuke Kaneda <kaneda@isas.jaxa.jp>
;; cvsinfo : $Id: seg.scm,v 1.6 2005/07/27 17:44:11 kaneda Exp $
;;----------------------------------------------------------------------------
(list 
 (cons 'modelSwarm
       (make-instance 'SegregatedModelSwarm
                      #:worldXSize 80
                      #:worldYSize 80
                      #:seedChurch 0.01
                      #:raceNum 3
                      #:seedPhilan 0.01
                      #:personDensity 0.8)))

;;----------------------------------------------------------------------------
;; end of file
;;----------------------------------------------------------------------------
