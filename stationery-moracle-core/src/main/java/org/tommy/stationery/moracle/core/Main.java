package org.tommy.stationery.moracle.core;

import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MoracleReturnData;
import org.tommy.stationery.moracle.core.optimizer.Optimizer;


public class Main{
	public static void main(String args[]) throws Exception {
        //optimizer.parse("SELECT A.a4, sum(cast(A.a7,int)) FROM test1 A group by A.a4 order by sum(cast(A.a7,int))");
        //optimizer.parse("SELECT distinct A.a2, sum(cast(A.a18,int)) FROM test1 A, test3 B where A.a18 = B.a18 order by A.a18 desc");
        //SELECT a1,a2,a3,a4,a5 FROM test3
        //SELECT a0,a1,a2,a3,a4,a5 FROM test1(a2 like '%골프%')'

       double t =  System.currentTimeMillis();
        String sql = "SELECT a1,a2,a3,a4,a5 FROM test2";

        ///daum/tmp/
        String url = "inputPath=/Users/kun7788/Desktop/input/&seperator=,&fileExtension=.csv&fileEncoding=MS949&isHeader=Y";
        Config config = new Config(Config.decode(url));

        Optimizer optimizer = new Optimizer(config);
        MoracleReturnData returlUrl = optimizer.start(sql);

        System.out.println(returlUrl.getUrl());
        System.out.println("elapse time : " + (System.currentTimeMillis() - t)/1000 + "sec");
    }
}