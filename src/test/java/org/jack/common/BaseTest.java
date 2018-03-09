package org.jack.common;

public abstract class BaseTest {
	protected void log(double...msgs) {
		for(double msg:msgs){
			log(msg);
		}
	}
	protected void log(float...msgs) {
		for(float msg:msgs){
			log(msg);
		}
	}
	protected void log(long...msgs) {
		for(long msg:msgs){
			log(msg);
		}
	}
	protected void log(int...msgs) {
		for(int msg:msgs){
			log(msg);
		}
	}
	protected void log(char...msgs) {
		for(char msg:msgs){
			log(msg);
		}
	}
	protected void log(short...msgs) {
		for(short msg:msgs){
			log(msg);
		}
	}
	protected void log(byte...msgs) {
		for(byte msg:msgs){
			log(msg);
		}
	}
	protected void log(Object...msgs) {
		for(Object msg:msgs){
			log(msg);
		}
	}
	protected void log(Object msg) {
		System.out.println(msg);
	}
}
