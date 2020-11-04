package com.flyingchicken.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;



public class FlyingChicken extends ApplicationAdapter {
	private SpriteBatch batch;
	//1 dung texture de them hinh anh
	private Texture background;
	private int offset;
	//khai bao nhan vat voi cac hinh anh la mot o trong mang
	private Texture[] bird;
	//tao bien chong mat
	private Texture dizzy;
	//thay doi chi so trong mang hinh anh nhan vat bang cach tao ham state va tang ham
	private int state;
	//tao bien delay de giam toc do chuen dong cua nhan vat
	private int delay=0;
	//khai bao bien trong luc
	private float gravity=0.2f;
	private float velocity=0;

	private int birdY=0;
	//tao rectangle cho nhan vat
	private Rectangle birdRectangle;
	//tao bien diem
	private int score=0;
	//tao bien trang thai tro choi de chay case
	private int gameState=0;
	//lam choi diem xuat hien tren man hinh
	private BitmapFont font;


	//toa do x,y cua cac dong xu
	private ArrayList<Integer> coinXs=new ArrayList<Integer>();
	private ArrayList<Integer> coinYs=new ArrayList<Integer>();
	//tao 1 shape moi chua coin
	private ArrayList<Rectangle> coinRectangles=new ArrayList<Rectangle>();
	private Texture coin;
	private int coinCount;

	//lam bom giong y chang lam coin
	private ArrayList<Integer> bombXs=new ArrayList<Integer>();
	private ArrayList<Integer> bombYs=new ArrayList<Integer>();
	private ArrayList<Rectangle> bombRectangles=new ArrayList<Rectangle>();
	private Texture bomb;
	private int bombCount;

	//tao mot ham random cho chieu cao ma cac dong xu, bom se xuat hien
	private Random random;

	private Music coinsound;
//	private Music chicsnd;




	
	@Override
	//duoc tao ra lan dau khi mo game, chuan bi cho game bat dau chay, dua nhưng cong viec ma chung ta chi muon thuc hien mot lam vao ham nay
	public void create () {
		//dung batch de hien thi nhung doi tuong len man hinh, dung texture de luu tru hinh anh
		batch = new SpriteBatch();

		//2 chuan bi hinh nen
		background=new Texture("bg.png");
		offset=0;
		// tao nhan vat, dua cac hinh anh vao mang
		bird =new Texture[4];
		bird[0]=new Texture("frame-1.png");
		bird[1]=new Texture("frame-2.png");
		bird[2]=new Texture("frame-3.png");
		bird[3]=new Texture("frame-4.png");

		birdY=Gdx.graphics.getHeight()/2;

		//set up cho cac dong xu
		coin =new Texture("coin.png");
		//set up bom
		bomb =new Texture("bomb.png");

		//set up ham tao gia tri ngau nhien
		random=new Random();
		dizzy=new Texture("dizzy-1.png");
		//lam diem xuat hien
		font=new BitmapFont();
		//mau chu cua diem
		font.setColor(Color.WHITE);
		//kich thuoc diem
		font.getData().setScale(10);

		coinsound=Gdx.audio.newMusic(Gdx.files.internal("coin3.wav"));
//		chicsnd=Gdx.audio.newMusic(Gdx.files.internal("birdflap.flac"));

	}
	//dung mot ham de tao ra cac dong xu,tao method tao xu
	public void makeCoin(){
		// dong xu can duoc xuat hien o do cao bat ki-> dung ham random
		//cho gia tri ngau nhien khoang 0 va 1, nhan voi do cao cua man hinh
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		//vi tri y cua dong xu se la mot random nhu o tren
		coinYs.add((int)height);
		//vi tri x cua 1 dong xu se nhu nhau trong toan bo trang thai
		coinXs.add(Gdx.graphics.getWidth());
	}
	//chuc nang tao bomb tuong tu
	public void makeBomb(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}


	@Override
	//ham se lap di lap lai cho toi khi ban ket thuc tro choi, o mot so thoi diem ham dispose co the duoc goi, de ket thuc tro choi, nhung trong tro choi nay  se khong can goi ham dispose
	public void render () {
		//de bat dau can goi ham batch.begin
		batch.begin();
		//lam hinh nen chuyen dong
		//tang  1 don vi moi khi render dc goi
		offset++;
		//neu bien offset tang len qua cao thi dat no ve 0
		if(offset % Gdx.graphics.getWidth()==0){
			offset=0;
		}
		//hien thi hinh anh tren man hinh, diem bat dau (o goc duoi cung ben trai)diem ket thuc chieu cao (lay chieu cao cua man hinh) do rong (lay do rong cua man hinh)
		//dat x=-offset de lam cham hinh nen tren man hinh
		batch.draw(background,-offset,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.draw(background,-offset+Gdx.graphics.getWidth(),0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState==1){
			//tro choi dang chay
			//lam cac dong xu xuat hien tren man hinh, xuat hien trc nhan vat de gay hung thu cho nguoi dung
			//moi lan bien coin count tang toi 100 se xuat hien mot dong xu tren man hinh. hoat dong nhu ham delay
			if(coinCount<100){
				coinCount++;}
			else{
				coinCount=0;
				makeCoin();
			}
			//truoc khi vao vonmg lap can xoa nhng thu xuat hien trong coin
			coinRectangles.clear();
			//lam xuat hien cac dong xu tren man hinh, dung vong lap for vi chua biet duoc so luong dong xu se co
			for(int i=0; i < coinXs.size();i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				//vi tri x cua dong xu duoc dinh san, va 2 dong xu lien tiep cach nhau 4
				coinXs.set(i,coinXs.get(i)-4);
				//them mot hinh rectangle vao man hinh voi toa do x, y, chieu rong = chieurong coin, chieu cao= chieu cao coin
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			//chinh sua thoi gian delay de tao bomb dai hon
			if(bombCount<250){
				bombCount++;}
			else{
				bombCount=0;
				makeBomb();
			}
			bombRectangles.clear();
			for(int i=0; i < bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				//vi tri x cua bom duoc dinh san, va 2 bomb lien tiep cach nhau 8
				bombXs.set(i,bombXs.get(i)-8);
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}


			//xu li voi event cham bang Gdx, khi co ng chan vao man hinh dk se cho ra ket qua true
			if(Gdx.input.justTouched()){
				velocity=-10;
//				chicsnd.play();
				coinsound.setVolume(0.5f);
			}

			//thay doi chi so hinh anh nhan vat
			// tam lan chay vong lap delay thi chi so mang se duoc thay doi 1 lan-> giam toc do nhan vat
			if(delay<8){
				delay++;
			}else{
				delay=0;
				if(state<3){
					state++;
				}else{
					state=0;
				}
			}
			//tinh toan velocity cua nhan vat
			//-10+gravity
			velocity=velocity+gravity;
			//0-(-9.8)->+9.8
			birdY-=velocity;
			//neu nhu nhan vat toi duoi cung man hinh, nhan vat se dung lai
			if(birdY <= 0 ){
				birdY=0;
			}
		}else if(gameState==0){
			//khoi dong tro choi
			//neu nguoi choi cham vao man hinh, tro choi se dc khhoi dong
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}else if(gameState==2){
			//tro choi ket thuc

			//khoi dong lai moi thu
			if(Gdx.input.justTouched()){
				gameState=1;
				birdY=Gdx.graphics.getHeight()/2;
				score=0;
				velocity=0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount=0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount=0;

			}

		}
		if(gameState==2){
			//hien thi bird dau co sao
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - bird[state].getWidth() / 2, birdY);
		}else {
			//bien birdY de cap nhat vi tri cua nhan vat ( khi bat dau tro choi hay khi roi)// dua nhan vat len man hinh
			// dun nhan vat ra giua man hinh bang cach lay chieu rong-kich thuoc nhan vat va chieu cao man hinh chia 2
			//dua bien state vao chi so mang, de thay doi hinh anh nhan vat tao hieu ung chuyen dong, thay toa do y bang bien birY <-- cap nhat vi tri cua nhan vat
			batch.draw(bird[state], Gdx.graphics.getWidth()/2  - bird[state].getWidth() / 2, birdY);
		}
		//tao gdx shape cho nhan vat
		birdRectangle=new Rectangle(Gdx.graphics.getWidth()/2 - bird[state].getWidth() / 2,birdY,bird[state].getWidth(),bird[state].getHeight());
		//kiem tra xem vung rectangle nhan vat cho trung voi vung cua xu khong
		for(int i=0;i<coinRectangles.size();i++){
			//chinh lai import
			if(Intersector.overlaps(birdRectangle,coinRectangles.get(i))){
				//cong 1 diem moi khi va cham vao dong xu
				score++;
				coinsound.play();
				coinsound.setVolume(0.4f);

				//lam dong xu bien mat khi va cham
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
		for(int i=0;i<bombRectangles.size();i++){
			//chinh lai import
			if(Intersector.overlaps(birdRectangle,bombRectangles.get(i))){

				

				//trang thai tro choi ket thuc
				gameState=2;
			}
		}
		//lam bien diem xuat hien,vi tri x,y
		font.draw(batch,String.valueOf(score),100,200);

		//de ket thuc can goi ham batch.end
		batch.end();
	}


		@Override
	public void dispose () {
		batch.dispose();

	}
}
