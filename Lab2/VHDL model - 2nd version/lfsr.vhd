library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

-- this is an asynchronoous parallel-in serial-out LFSR
entity lfsr is
    Port ( clk : in  STD_LOGIC;
           ld  : in STD_LOGIC;
           data: in  STD_LOGIC_VECTOR(15 downto 0) := (OTHERS => '0');
           R   : out STD_LOGIC
			);
end lfsr;

-- there can be many architectures (aka "ways things work") 
-- defined for a given entity. here we define two, that differ
-- in places where taps are located
ARCHITECTURE first OF lfsr IS
  -- this will store internal state of LFSR
  -- initialise it to all-zeroes
  signal q : STD_LOGIC_VECTOR(15 downto 0) := (OTHERS => '0');
BEGIN

  -- this process will be executed each time
  -- a change in either of the signals: clk, ld, data
  -- is detected. this is the "sensitivity list"
  PROCESS(clk, ld, data)
  BEGIN
    -- note that if 'ld = 1' then, regardless of clk the LFSR
    -- will read external data; that's why it's __asynchronous__
    if(ld = '1') 
    then
      q <= data;
    -- however, if 'ld' is not operational, then 'clk' will 
    -- cause the state change
    elsif(clk'event and clk = '1')
    then
      -- cyclic shift - as simple as that
	    q(15 downto 1) <= q(14 downto 0);
      -- taps at bits 15, 14, 13 and 4
	    q(0) <= not(q(15) XOR q(14) XOR q(13) XOR q(4));
    end if;
  END PROCESS;

  -- this is not a part of the process - this assignment is
  -- permanent, i.e. "it's always there" - just like a wire 
  -- connecting MSB to the output
  R <= q(15);
	
END first;

-- another architecture of the same entity 'lfsr'
ARCHITECTURE second OF lfsr IS
  signal q : STD_LOGIC_VECTOR(15 downto 0) := (OTHERS => '0');
BEGIN

  PROCESS(clk, ld, data)
  BEGIN
    if(ld = '1') 
    then
      q <= data;
    elsif(clk'event and clk = '1')
    then
	    q(15 downto 1) <= q(14 downto 0);
      -- taps at bits 15, 10, 9 and 1
	    q(0) <= not(q(15) XOR q(10) XOR q(9) XOR q(1));
    end if;
  END PROCESS;

  R <= q(15);
	
END second;


