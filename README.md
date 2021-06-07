# Picofoxy
**P**ipelined **I**n-order **Co**re **fo**r Arti**x**-7 Art**y**-35T board

Picofoxy is a minimal system on a chip based on a RISC-V processor that is designed to be run on the Arty-35T FPGA board. 
## Motivation
Create a minimal SoC built from Chisel based generators and use a completely open-source toolchain to port the core onto the FPGA.

## Dependencies
1. SBT
2. Symbiflow
3. OpenOCD

## TL;DR

Clone the repository to the location where you want the project to live in:

```bash
git clone https://github.com/merledu/picofoxy.git
```
Move into the repository and clone the dependent projects which are integrated as git submodules:

```bash
cd picofoxy
git submodule update --init --recursive
```
Genrate the RTL:
```bash
make picofoxy
```

Create the bitstream:

__Note:__ You should be inside your conda environment for this command to work. (See below on how to set it up)
```bash
make bitstream
```

Upload the bitstream on Arty A7 board:
```bash
make upload
```

## Picofoxy in action
<img src="https://github.com/merledu/picofoxy/blob/main/symbiflow-running.gif" width="300" height="500" />

## Getting the dependencies
#### JDK 8 or newer

We recommend LTS releases Java 8 and Java 11. You can install the JDK as recommended by your operating system, or use the prebuilt binaries from [AdoptOpenJDK](https://adoptopenjdk.net/).

#### SBT

SBT is the most common built tool in the Scala community. You can download it [here](https://www.scala-sbt.org/download.html).

#### Symbiflow

Symbiflow can be easily installed for the Xilinx Series 7 boards from [here](https://github.com/merledu/symbiflow-magic) follow the README till you can activate your conda environment.

#### OpenOCD
You can easily install OpenOCD on Ubuntu:
```bash
sudo apt install openocd
```

## Running new program
Write now baremetal assembly is supported in Picofoxy. The gpio base address is `0x40001000`. There is a `DIRECT_OE` register at offset `0x1C`. It configures the pins as outputs. The `DATA_OUT` register at offset `0x10` is then used to write the data on the pins. Write now 4 pins gpio[3:0] are extracted from the top and connected with the Arty board. However, these can be extended upto 32 pins. Here is a dummy program to turn on the gpio[0] pin attached with LD4 of Arty board.

```asm
li x15, 0x40001000  # load gpio base address
li x16, 1
sw x16, 0x1c(x15)   # configure gpio[0] pin as output
sw x16, 0x10(x15)   # turn gpio[0] pin HIGH
exit:
  jal exit          # keeping the pc in loop
```
The compiled hex should be added in the `program.mem` file inside the `fpga/` folder.
