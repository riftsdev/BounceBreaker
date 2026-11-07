package com.kg.bouncebreaker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.graphics.*;
import java.util.*;

public class MainActivity extends Activity {
  @Override public void onCreate(Bundle b) { super.onCreate(b); getWindow().getDecorView().setSystemUiVisibility(5894|1024|512); setContentView(new Game()); }
  class Block {float x,y,w,h; int hp; Block(float x,float y,float w,float h,int hp){this.x=x;this.y=y;this.w=w;this.h=h;this.hp=hp;}}
  class Particle {float x,y,vx,vy,life;int color;Particle(float x,float y,int c){this.x=x;this.y=y;color=c;vx=(float)(Math.random()*440-220);vy=(float)(Math.random()*440-220);life=.65f;}}
  class Game extends View {
    Canvas c; Paint p=new Paint(3); ArrayList<Block> blocks=new ArrayList<>();ArrayList<Particle> particles=new ArrayList<>();
    float w,h,bx,by,vx=190,vy=-250,r=17,tx,ty,downX,downY; boolean aiming=false,started=false;int level=1,score=0;long last=0;
    Game(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    void init(){w=getWidth();h=getHeight();bx=w/2;by=h*.77f;vx=180;vy=-250;blocks.clear();particles.clear();Random random=new Random(level*129L);int cols=5;float margin=18,gap=7,bw=(w-margin*2-gap*(cols-1))/cols;for(int row=0;row<Math.min(3+level,8);row++)for(int col=0;col<cols;col++)if(random.nextFloat()>.15f)blocks.add(new Block(margin+col*(bw+gap),h*.17f+row*48,bw,38,1+level/3+random.nextInt(2)));started=true;}
    void color(int co){p.setColor(co);p.setStyle(Paint.Style.FILL);p.setStrokeWidth(1);}
    void text(String s,float x,float y,int size,int co){color(co);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(size);c.drawText(s,x,y,p);}
    void burst(float x,float y,int co){for(int i=0;i<12;i++)particles.add(new Particle(x,y,co));}
    void update(float dt){if(!started)return;float oldX=bx,oldY=by;vy+=380*dt;bx+=vx*dt;by+=vy*dt;if(bx<r){bx=r;vx=Math.abs(vx);}if(bx>w-r){bx=w-r;vx=-Math.abs(vx);}if(by<r+70){by=r+70;vy=Math.abs(vy);}if(by>h-r-18){by=h-r-18;vy=-Math.abs(vy)*.94f; if(Math.abs(vy)<240)vy=-360;}
      for(int i=blocks.size()-1;i>=0;i--){Block b=blocks.get(i);float nx=Math.max(b.x,Math.min(bx,b.x+b.w)),ny=Math.max(b.y,Math.min(by,b.y+b.h));float dx=bx-nx,dy=by-ny;if(dx*dx+dy*dy<r*r){if(oldY+r<=b.y||oldY-r>=b.y+b.h)vy=-vy;else vx=-vx;bx=oldX;by=oldY;b.hp--;score+=10;burst(nx,ny,Color.rgb(255,192,70));if(b.hp<=0){blocks.remove(i);score+=40;}break;}}
      float speed=(float)Math.hypot(vx,vy);if(speed>850){vx*=850/speed;vy*=850/speed;}if(blocks.isEmpty()){level++;init();}
      for(int i=particles.size()-1;i>=0;i--){Particle q=particles.get(i);q.x+=q.vx*dt;q.y+=q.vy*dt;q.vy+=250*dt;q.life-=dt;if(q.life<=0)particles.remove(i);}
    }
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);c=canvas;if(getWidth()==0)return;if(!started)init();long now=System.nanoTime();float dt=last==0?0:Math.min(.033f,(now-last)/1e9f);last=now;if(!aiming)update(dt);c.drawColor(Color.rgb(12,17,35));color(Color.rgb(25,34,62));for(int i=0;i<25;i++)c.drawCircle((i*113)%Math.max(1,(int)w),(i*173)%Math.max(1,(int)h),2,p);
      text("BOUNCE BREAKER",20,47,25,Color.WHITE);text("FASE "+level+"    PONTOS "+score,20,77,17,Color.rgb(151,206,255));
      for(Block b:blocks){int co=b.hp==1?Color.rgb(64,215,181):b.hp==2?Color.rgb(73,156,255):b.hp==3?Color.rgb(255,178,69):Color.rgb(235,90,132);color(co);c.drawRoundRect(b.x,b.y,b.x+b.w,b.y+b.h,10,10,p);text(""+b.hp,b.x+b.w/2-5,b.y+26,18,Color.WHITE);}
      for(Particle q:particles){color(q.color);p.setAlpha((int)(255*Math.min(1,q.life/.65f)));c.drawCircle(q.x,q.y,4,p);p.setAlpha(255);}
      color(Color.rgb(94,213,255));p.setShadowLayer(19,0,0,Color.CYAN);c.drawCircle(bx,by,r,p);p.clearShadowLayer();color(Color.WHITE);c.drawCircle(bx-5,by-6,5,p);
      if(aiming){float dx=bx-tx,dy=by-ty,mag=(float)Math.hypot(dx,dy);if(mag>1){float factor=Math.min(120,mag)/mag;color(Color.rgb(140,220,255));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(4);c.drawLine(bx,by,bx+dx*factor*2,by+dy*factor*2,p);p.setStyle(Paint.Style.FILL);for(int i=1;i<=4;i++)c.drawCircle(bx+dx*factor*i*.42f,by+dy*factor*i*.42f,5,p);}}
      text("TOQUE, PUXE E SOLTE PARA LANÇAR",18,h-35,15,Color.rgb(182,194,222));invalidate();}
    @Override public boolean onTouchEvent(MotionEvent e){float x=e.getX(),y=e.getY();switch(e.getActionMasked()){case MotionEvent.ACTION_DOWN:if(y<100&&x>w*.72f){level=1;score=0;init();return true;}aiming=true;tx=x;ty=y;downX=x;downY=y;return true;case MotionEvent.ACTION_MOVE:tx=x;ty=y;return true;case MotionEvent.ACTION_UP:if(aiming){float dx=downX-x,dy=downY-y;float len=(float)Math.hypot(dx,dy);if(len>10){float power=Math.min(800,Math.max(320,len*5));vx=dx/len*power;vy=dy/len*power;}aiming=false;}return true;}return true;}
  }
}
