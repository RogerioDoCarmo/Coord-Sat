/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.NTRIP;
/**
 *
 * @author Fernanda
 */

//** gera um valor aproximado de Math.pow
public class Mathm {
    public static double pow(double x, double y)
	{
		return powTaylor(x,y);
	}

	public double powSqrt(double x, double y)
	{
		int den = 1024, num = (int)(y*den), iterations = 10;
		double n = Double.MAX_VALUE;

		while( n >= Double.MAX_VALUE && iterations > 1)
		{
			n = x;

			for( int i=1; i < num; i++ )n*=x;

			if( n >= Double.MAX_VALUE )
			{
				iterations--;
				den = (int)(den / 2);
				num = (int)(y*den);
			}
		}

		for( int i = 0; i <iterations; i++ )n = Math.sqrt(n);
   //System.out.printf("\nn: "+n);
		return n;
	}

	public double powDecay(double x, double y)
	{
		int num, den = 1001, s = 0;
		double n = x, z = Double.MAX_VALUE;

		for( int i = 1; i < s; i++)n *= x;

		while( z >= Double.MAX_VALUE )
		{
			den -=1;
			num = (int)(y*den);
			s = (num/den)+1;

			z = x;
			for( int i = 1; i < num; i++ )z *= x;
		}

		while( n > 0 )
		{
			double a = n;

			for( int i = 1; i < den; i++ )a *= n;

			if( (a-z) < .00001 || (z-a) > .00001 ) return n;

			n *= .9999;
		}

		return -1.0;
	}

	public static double powTaylor(double a, double b)
	{
		boolean gt1 = (Math.sqrt((a-1)*(a-1)) <= 1)? false:true;
		int oc = -1,iter = 30;
		double p = a, x, x2, sumX, sumY;

		if( (b-Math.floor(b)) == 0 )
		{
			for( int i = 1; i < b; i++ )p *= a;
			return p;
		}

		x = (gt1)?(a /(a-1)):(a-1);
		sumX = (gt1)?(1/x):x;

		for( int i = 2; i < iter; i++ )
		{
			p = x;
			for( int j = 1; j < i; j++)p *= x;

			double xTemp = (gt1)?(1/(i*p)):(p/i);

			sumX = (gt1)?(sumX+xTemp):(sumX+(xTemp*oc));

			oc *= -1;
		}

		x2 = b * sumX;
		sumY = 1+x2;

		for( int i = 2; i <= iter; i++ )
		{
			p = x2;
			for( int j = 1; j < i; j++)p *= x2;

			int yTemp = 2;
			for( int j = i; j > 2; j-- )yTemp *= j;

			sumY += p/yTemp;
		}
             // System.out.printf("\nsumY: "+sumY);
		return sumY;
	}

}
