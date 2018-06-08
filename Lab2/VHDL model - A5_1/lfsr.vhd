library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

-- this is an asynchronous parallel-in serial-out LFSR with 19 bits long
entity lfsr1 is
    Port ( clk : in  STD_LOGIC;
           ld  : in STD_LOGIC;
           data: in  STD_LOGIC_VECTOR(18 downto 0) := (OTHERS => '0');
           R   : out STD_LOGIC
			);
end lfsr1;

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

-- this is an asynchronous parallel-in serial-out LFSR with 22 bits long
entity lfsr2 is
    Port ( clk : in  STD_LOGIC;
           ld  : in STD_LOGIC;
           data: in  STD_LOGIC_VECTOR(21 downto 0) := (OTHERS => '0');
           R   : out STD_LOGIC
			);
end lfsr2;

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

-- this is an asynchronous parallel-in serial-out LFSR with 23 bits long
entity lfsr3 is
    Port ( clk : in  STD_LOGIC;
           ld  : in STD_LOGIC;
           data: in  STD_LOGIC_VECTOR(22 downto 0) := (OTHERS => '0');
           R   : out STD_LOGIC
			);
end lfsr3;

-- there can be many architectures (aka "ways things work") 
-- defined for a given entity. here we define three, that differ
-- in places where taps are located
ARCHITECTURE first OF lfsr1 IS
  -- this will store internal state of LFSR
  -- initialise it to all-zeroes
  signal q : STD_LOGIC_VECTOR(18 downto 0) := (OTHERS => '0');
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
	    q(18 downto 1) <= q(17 downto 0);
      -- taps at bits 13, 16, 17 and 18
	    q(0) <= q(13) XOR q(16) XOR q(17) XOR q(18);
    end if;
  END PROCESS;

  -- this is not a part of the process - this assignment is
  -- permanent, i.e. "it's always there" - just like a wire 
  -- connecting MSB to the output
  R <= q(18);
	
END first;

-- another architecture of the same entity 'lfsr'
ARCHITECTURE second OF lfsr2 IS
  -- this will store internal state of LFSR
  -- initialise it to all-zeroes
  signal q : STD_LOGIC_VECTOR(21 downto 0) := (OTHERS => '0');
BEGIN

  PROCESS(clk, ld, data)
  BEGIN
    if(ld = '1') 
    then
      q <= data;
    elsif(clk'event and clk = '1')
    then
	    q(21 downto 1) <= q(20 downto 0);
      -- taps at bits 20, and 21
	    q(0) <= q(20) XOR q(21);
    end if;
  END PROCESS;

  R <= q(21);
	
END second;

-- another architecture of the same entity 'lfsr'
ARCHITECTURE third OF lfsr3 IS
  -- this will store internal state of LFSR
  -- initialise it to all-zeroes
  signal q : STD_LOGIC_VECTOR(22 downto 0) := (OTHERS => '0');
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
	    q(22 downto 1) <= q(21 downto 0);
      -- taps at bits 7, 20, 21 and 22
	    q(0) <= q(7) XOR q(20) XOR q(21) XOR q(22);
    end if;
  END PROCESS;

  -- this is not a part of the process - this assignment is
  -- permanent, i.e. "it's always there" - just like a wire 
  -- connecting MSB to the output
  R <= q(22);
	
END third;