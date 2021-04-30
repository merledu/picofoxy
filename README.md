# Picofoxy
**P**ipelined **I**n-order **Co**re **fo**r Arti**x**-7 Art**y**-35T board

Picofoxy is a minimal system on a chip based on a RISC-V processor that is designed to be run on the Arty-35T FPGA board. 
## Motivation
Create a minimal SoC built from Chisel based generators and use a completely open-source toolchain to port the core onto the FPGA.

### Plan/Goals
* Use Buraq-mini as the core
* Use Caravan to generate the Wishbone bus
* Use Jigsaw to generate Block RAMs and the GPIO peripheral
* Interconnect together to create an SoC
* Use FuseSoC to generate the bitstream
* Use openFPGALoader to program the bitstream
