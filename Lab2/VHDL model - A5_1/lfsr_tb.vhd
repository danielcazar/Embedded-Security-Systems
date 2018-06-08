library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;
use IEEE.STD_LOGIC_TEXTIO.all;
use std.textio.all;

-- this is a testbench, but can be any other entity
entity lfsr_tb is
  end lfsr_tb;

architecture complex of lfsr_tb is
  -- this clock runs at HOW MANY MHz?
  constant clock_period : time := 10 ns;
  --a 1 nS period corresponds to 1 GHz
  --1000 nS => 1 microsecond => 1 MHz
  --10ns=10GHz

  -- clock signal
  signal clock : std_logic :=  '0';
  -- lines for loading-up LFSRs
  signal q1 : std_logic_vector(18 downto 0) := (others => '1');
  signal q2 : std_logic_vector(21 downto 0) := (others => '1');
  signal q3 : std_logic_vector(22 downto 0) := (others => '1');
  -- signal to start loading LFSRs
  signal load  : std_logic := '0';
  -- outputs from LFSRs
  signal LFSRA,LFSRB,LFSRC : std_logic;
  -- signal used to calculate XOR of outputs of three LFSRs
  signal RND : std_logic;
  
  -- just a reminder what will be tested
  component lfsr1 
    port( 
          clk  : in STD_LOGIC; 
          ld   : in STD_LOGIC;  
          data : in STD_LOGIC_VECTOR(18 downto 0);
          R    : out STD_LOGIC );
  end component;

  component lfsr2
    port(
		  clk : in  STD_LOGIC;
          ld  : in STD_LOGIC;
          data: in  STD_LOGIC_VECTOR(21 downto 0);
          R   : out STD_LOGIC );
  end component;
  
  component lfsr3
    port(
		  clk : in  STD_LOGIC;
          ld  : in STD_LOGIC;
          data: in  STD_LOGIC_VECTOR(22 downto 0);
          R   : out STD_LOGIC );
  end component;

  
  
  -- remember? we defined three architectures for 'lfsr'
  for UUT1 : lfsr1 use entity work.lfsr1(first);
  for UUT2 : lfsr2 use entity work.lfsr2(second);
  for UUT3 : lfsr3 use entity work.lfsr3(third);

  
  

  
begin
  -- let's create instances of our LFSRs
  UUT1 : lfsr1 port map ( clk => clock, ld => load, data => q1, R => LFSRA );
  UUT2 : lfsr2 port map ( clk => clock, ld => load, data => q2, R => LFSRB );
  UUT3 : lfsr3 port map ( clk => clock, ld => load, data => q3, R => LFSRC );
	
	  
  
  -- this will run infinitely, stopping every few ns
  clocker : process
  variable v_OLINE : line;
  begin
    clock <= not clock;
	wait for clock_period/2;
	
	write(v_OLINE, RND);
	writeline(output, v_OLINE);
	
  end process;
  
  -- this will run once and then wait for ever
  init : process 

  begin	
  
    -- time to tell LFSRs to load up some data
    load <= '1';
    -- and give it to them (to one of them, at least)
	q1 <= "0000000000000000001";
	q2 <= "0000000000000000000001";
	q3 <= "00000000000000000000001";
    -- even though LFSRs are async, let's wait for a bit...
    wait until clock'event and clock = '0';
    -- ... and let them run freely
    load <= '0';
	
	-- this process is finished, make it wait ad infinitum	
    wait;
  end process;
	

	--calculate XOR of three outputs of LFSRs
    RND <= LFSRA xor LFSRB xor LFSRC;
	
	


  -- okay, what's going on here? well, the 'clocker' process 
  -- keeps running, changing clk -> NOT clk -> clk -> NOT clk ...
  -- and clk is fed to LFSRs, so they are busy working
  -- the simulation will continue until you kill it, or specify 
  -- the stop time using '--stop-time=XXX' switch to ghdl 

end complex;