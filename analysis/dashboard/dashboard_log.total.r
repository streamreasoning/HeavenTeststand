#THIS SCRIPT GENERATES THE PLOT FOR THE DIAGONAL K=1 IN THE CONFIGURATION TOTAL_TRIPLE_IN_WINDOW/EW PLOTTING THE FOUR BASELINES IN THE SAME FIGURE IN 4 DIFFERENT PLOTS
#IT USES LOG SCALING

log_plot_dash <- function(x,y,sdx,sdy, col, pch){
  
  lx <- log(x)
  ly <- log(y)
  
  par(new=TRUE)
  points(x = lx, y = ly, col=col, type = "p", pch=pch)
  #arrows(lx, log(y-sdy),lx,log(y+sdy),, length=0.05, angle=90, code=3, col="grey55")
  #arrows(log(sdx), ly,log(x+2*sdx),ly, length=0.05, angle=90, code=3, col="gray55")
  
}

log_dashboard<- function(a,b,c,d, xlim,ylim,ss = c(FALSE, FALSE, FALSE, FALSE, FALSE)){
  
  exp <- paste(lat_report_rhodf$STREAM[a], substr(lat_report_rhodf$REASONING[a], 0, 6))
  
  myxlim <- c(4.2, log(xlim[2]))
  myylim <- c(5, 8)
  
  
  plot(seq(min(xlim[1], ylim[1]),max(xlim[2], ylim[2]),0.001),seq(min(xlim[1], ylim[1]),max(xlim[2], ylim[2]),0.001), log="xy",  type = "n", ylab = "Memory", xlab = "Latency", xlim=myxlim, ylim=myylim)
  
  abline( h = seq(0.001, 10, 0.1 ), lty = 3, col = colors()[ 440 ] )
  abline( v = seq(0.001, 10, 0.1 ), lty = 3, col = colors()[ 440 ] )
  
  
  z<-a
  if(ss[1]){
    pch <- 0
  }else{
    pch <- 15
  }
  pch_legend <- pch
  log_plot_dash(lat_report_rhodf$Mean.SS[z], mem_report_rhodf$A..Mean.SS[z], lat_report_rhodf$Dev.StdSS[z],mem_report_rhodf$A.Dev.Std.SS[z], "red", pch)
  
  z<-b
  if(ss[2]){
    pch <- 1
  }else{
    pch <- 16
  }
  pch_legend <- c(pch_legend,pch)
  log_plot_dash(lat_report_rhodf$Mean.SS[z], mem_report_rhodf$A..Mean.SS[z], lat_report_rhodf$Dev.StdSS[z], mem_report_rhodf$A.Dev.Std.SS[z], "blue", pch)
  
  
  z<-c
  if(ss[3]){
    pch <- 2
  }else{
    pch <- 17
  }
  pch_legend <- c(pch_legend,pch)
  log_plot_dash(lat_report_rhodf$Mean.SS[z], mem_report_rhodf$A..Mean.SS[z], lat_report_rhodf$Dev.StdSS[z], mem_report_rhodf$A.Dev.Std.SS[z], "black", pch)
  
  z <- d
  if(ss[4]){
    pch <- 5
  }else{
    pch <- 18
  }
  pch_legend <- c(pch_legend,pch)
  log_plot_dash(lat_report_rhodf$Mean.SS[z], mem_report_rhodf$A..Mean.SS[z], lat_report_rhodf$Dev.StdSS[z], mem_report_rhodf$A.Dev.Std.SS[z], "chartreuse4", pch)
  
  
    legend(x=4.5,y=8,pch=pch_legend,bg = "white",bty="o", legend=c(
    paste( "K", lat_report_rhodf$K[a], "EW", lat_report_rhodf$EW[a]), 
    paste( "K", lat_report_rhodf$K[b], "EW", lat_report_rhodf$EW[b]), 
    paste( "K", lat_report_rhodf$K[c], "EW", lat_report_rhodf$EW[c]), 
    paste( "K", lat_report_rhodf$K[d], "EW", lat_report_rhodf$EW[d])), cex=1 ,y.intersp=1, col=c("red","blue","black","chartreuse4", "purple"))
  title(exp, paste("ENs",  lat_report_rhodf$EN[a], lat_report_rhodf$EN[b], lat_report_rhodf$EN[c], lat_report_rhodf$EN[d]))
  
}


png("DASHBOARD K*EW=10000 LOG DIAGONAL.png", 1000, 1000)

mem_report_rhodf <- mem_report_rhodf[with(mem_report_rhodf, order(K*EW, decreasing = TRUE)),]
lat_report_rhodf <- lat_report_rhodf[with(lat_report_rhodf, order(K*EW, decreasing = TRUE)),]

a <- 1
b <- a+1
c <- b+1
d <- c+1
split.screen(c(2,1))
split.screen(c(1,2), screen=1)

xlim <- c(min(lat_report_rhodf$Mean.SS), max(lat_report_rhodf$Mean.SS))
ylim <- c(sort(mem_report_rhodf$A..Mean.SS)[1], sort(mem_report_rhodf$A..Mean.SS, decreasing = TRUE)[1])


#INC GRAPH
screen(3)
log_dashboard(a,a+4,a+8,a+12,xlim,ylim, c(FALSE, TRUE, FALSE, FALSE, TRUE))
#INC STMT
screen(4) 
log_dashboard(b,b+4,b+8,b+12,xlim,ylim, c(FALSE, TRUE, FALSE, FALSE, TRUE))
split.screen(c(1,2), screen=2)


#NAIVE GRAPH
screen(5)
log_dashboard(c,c+4,c+8,c+12,xlim,ylim, c(TRUE, TRUE, FALSE, FALSE, TRUE))
screen(6)
#NAIVE STMT
log_dashboard(d,d+4,d+8,d+12,xlim,ylim, c(FALSE, TRUE, FALSE, FALSE, TRUE))

dev.off()




