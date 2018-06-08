entity adder is
  -- `a`, `b` and `c` are inputs of the adder.
  -- `x` and `y` are the outputs.
  port (a, b, c : in bit; x, y : out bit);
end adder;

architecture rtl of adder is
begin
   --  This full-adder architecture contains two concurrent assignment.
   -- Compute logical circuit outputs x and y
   -- not((not (b or a)) or (not (not (c or b)))) => x
   -- Truth table generator http://web.stanford.edu/class/cs103/tools/truth-table-tool/
   x <= (not (b or a)) nor (not (c nor b));
   -- (not (not (c or b))) and (not ((a /\ ~c)\/(~a/\c))) 
   y <= (not (c nor b)) and (not (a xor c));
end rtl;