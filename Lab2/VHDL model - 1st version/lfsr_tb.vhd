library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

-- this is a testbench, but can be any other entity
entity lfsr_tb is
  end lfsr_tb;

architecture complex of lfsr_tb is
  -- this clock runs at HOW MANY MHz?
  constant clock_period : time := 10 ns;

  -- clock signal
  signal clock : std_logic :=  '0';
  -- lines for loading-up LFSRs
  signal q1,q2 : std_logic_vector(15 downto 0) := (others => '0');
  -- signal to start loading LFSRs
  signal load  : std_logic := '0';
  -- outputs from LFSRs
  signal LFSR1,LFSR2 : std_logic;

  -- just a reminder what will be tested
  component lfsr 
    port( 
          clk  : in STD_LOGIC; 
          ld   : in STD_LOGIC;  
          data : in STD_LOGIC_VECTOR(15 downto 0);
          R    : out STD_LOGIC );
  end component;

  -- remember? we defined two architectures for 'lfsr'
  for UUT1 : lfsr use entity work.lfsr(first);
  for UUT2 : lfsr use entity work.lfsr(second);

begin
  -- let's create instances of our LFSRs
  UUT1 : lfsr port map ( clk => clock, ld => load, data => q1, R => LFSR1 );
  UUT2 : lfsr port map ( clk => clock, ld => load, data => q2, R => LFSR2 );

  -- this will run infinitely, stopping every few ns
  clocker : process
  begin
    clock <= not clock;
    wait for clock_period/2;
  end process;

  -- this will run once and then wait for ever
  init : process 
  begin
    -- time to tell LFSRs to load up some data
    load <= '1';
    -- and give it to them (to one of them, at least)
    q2 <= X"FAFA";
    -- even though LFSRs are async, let's wait for a bit...
    wait until clock'event and clock = '0';
    -- ... and let them run freely
    load <= '0';
    -- this process is finished, make it wait ad infinitum
    wait;
  end process;

  -- okay, what's going on here? well, the 'clocker' process 
  -- keeps running, changing clk -> NOT clk -> clk -> NOT clk ...
  -- and clk is fed to LFSRs, so they are busy working
  -- the simulation will continue until you kill it, or specify 
  -- the stop time using '--stop-time=XXX' switch to ghdl 

end complex;
