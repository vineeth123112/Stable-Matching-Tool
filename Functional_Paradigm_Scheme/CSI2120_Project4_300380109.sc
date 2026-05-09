#lang scheme

(define (read-f filename) (call-with-input-file filename
(lambda (input-port)
(let loop ((line (read-line input-port)))
(cond
 ((eof-object? line) '())
 (#t (begin (cons (string-split (clean-line line) ",") (loop (read-line input-port))))))))))
(define (format-resident lst)
  (list (car lst) (cadr lst) (caddr lst) (cdddr lst)))
(define (format-program lst)
  (list (car lst) (cadr lst) (string->number (caddr lst)) (map string->number(cdddr lst))))
(define (clean-line str)
  (list->string
   (filter (lambda (c) (not (or (char=? c #\") (char=? c #\[) (char=? c #\]) )))
           (string->list str))))
(define (read-residents filename)
(map (lambda(L) (format-resident (cons (string->number (car L)) (cdr L)))) (cdr (read-f filename))))
(define (read-programs filename)
(map format-program (cdr (read-f filename))))

(define PLIST (read-programs "programSmall.csv"))
(define RLIST (read-residents "residentSmall.csv"))

(define (get-resident-info rid rlist)
  (cond
    ((null? rlist) '())
    ((equal? rid (car (car rlist))) (car rlist))
    (else (get-resident-info rid (cdr rlist)))
  )
)

(define (get-program-info pid plist)
  (cond 
    ((null? plist) '())
    ((equal? pid (car (car plist))) (car plist))
    (else (get-resident-info pid (cdr plist)))
  )
)

(define (rank rid pinfo)
  (let loop ((rolList (cadddr pinfo))(index 0))
    (cond
      ((null? rolList) -1)
      ((equal? rid (car rolList)) index)
      (else (loop (cdr rolList) (+ index 1)))
    )
  )
)

(define (matched? rid matches)
  (cond
    ((null? matches) #f)
    ((member rid (map car (cadar matches))) #t)
    (else (matched? rid (cdr matches)))
  )
)

(define (get-match pid matches)
  (cond
    ((null? matches) '())
    ((eq? pid (caar matches)) (car matches))
    (else (get-match pid (cdr matches)))
  )
)

(define (add-resident-to-match pair match)
  (let ((pid (car match))(matchList (cadr match)))
    (list pid 
      (let loop ((matchList2 matchList))
        (cond
          ((null? matchList2)
           (list pair)
          )
          ((> (cdr pair) (cdar matchList2))
           (cons pair matchList2)
          )                                 
          (else
           (cons (car matchList2)(loop (cdr matchList2)))
          )
        )
      )
    )
  )
)

