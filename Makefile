current_dir = $(shell pwd)

picofoxy: clean rtl cleanup initmem

rtl:
	sbt "runMain PicofoxyDriver"

relocate: Picofoxy.v PLL_8MHz.v clk_wiz_0_clk_wiz.v
	mv -t fpga/ Picofoxy.v PLL_8MHz.v clk_wiz_0_clk_wiz.v	

cleanup: relocate
	rm -f firrtl_black_box_resource_files.f Picofoxy.fir Picofoxy.anno.json

initmem:
	sed -i '270i\initial begin \n $$readmemh("$(current_dir)/fpga/program.mem", mem); \n end' fpga/Picofoxy.v

clean: 
	rm -rf $(current_dir)/fpga/build/
	rm -rf $(current_dir)/fpga/Picofoxy.v $(current_dir)/fpga/PLL_8MHz.v $(current_dir)/fpga/clk_wiz_0_clk_wiz.v

bitstream: $(current_dir)/fpga/Picofoxy.v $(current_dir)/fpga/PLL_8MHz.v $(current_dir)/fpga/clk_wiz_0_clk_wiz.v
	TARGET="arty_35" $(MAKE) -C fpga

upload: $(current_dir)/fpga/build/arty_35/Picofoxy.bit
	openocd -f ${INSTALL_DIR}/${FPGA_FAM}/conda/envs/${FPGA_FAM}/share/openocd/scripts/board/digilent_arty.cfg -c "init; pld load 0 $(current_dir)/fpga/build/arty_35/Picofoxy.bit; exit"


