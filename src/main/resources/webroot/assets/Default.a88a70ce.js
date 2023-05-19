import{u as ae}from"./app.877f3e4d.js";import{g as Y,r as T,u as ne,c as w,t as F,a as r,m as z,d as O,o as I,b as N,w as C,e as pe,f as L,h as oe,i as D,j as le,C as ye,k as ue,l as re,p as ie,n as we,q as be,s as _e,T as Se,F as Ve,v as ke,x as P,y as Be,z as Te,A as Ce}from"./index.9753a940.js";import{V as xe,m as Me,a as Ee,b as $e,u as Ae,c as K,d as He,e as Pe,f as Re,t as Le,$ as De,g as Ie}from"./VAvatar.540c951f.js";import{m as Ne,V as Q,a as Ye,b as We}from"./VToolbar.ea7f237a.js";import{u as W,m as se,V as Fe}from"./tag.9461cb4b.js";import{m as ce,u as ve,a as ze,b as Oe,c as Xe}from"./layout.4d7ebf4d.js";import{u as X,V as R,a as qe}from"./VList.2e30d169.js";import{_ as Ze}from"./_plugin-vue_export-helper.cdc0426e.js";const Ue=Y()({name:"VAppBar",props:{modelValue:{type:Boolean,default:!0},location:{type:String,default:"top",validator:e=>["top","bottom"].includes(e)},...Ne(),...ce(),height:{type:[Number,String],default:64}},emits:{"update:modelValue":e=>!0},setup(e,l){let{slots:n}=l;const o=T(),a=ne(e,"modelValue"),t=w(()=>{var u,d,m,f;const p=(d=(u=o.value)==null?void 0:u.contentHeight)!=null?d:0,g=(f=(m=o.value)==null?void 0:m.extensionHeight)!=null?f:0;return p+g}),{ssrBootStyles:i}=X(),{layoutItemStyles:h}=ve({id:e.name,order:w(()=>parseInt(e.order,10)),position:F(e,"location"),layoutSize:t,elementSize:t,active:a,absolute:F(e,"absolute")});return W(()=>{const[p]=Q.filterProps(e);return r(Q,z({ref:o,class:["v-app-bar",{"v-app-bar--bottom":e.location==="bottom"}],style:{...h.value,height:void 0,...i.value}},p),n)}),{}}}),je=Y()({name:"VAppBarTitle",props:Ye(),setup(e,l){let{slots:n}=l;return W(()=>r(We,z(e,{class:"v-app-bar-title"}),n)),{}}}),Ge=O({__name:"AppBar",setup(e){const l=ae();function n(){l.changeDrawer()}return(o,a)=>(I(),N(Ue,{flat:""},{default:C(()=>[r(je,null,{default:C(()=>[r(xe,{icon:"mdi-minecraft",onClick:n}),pe(" MCServerControlPanel ")]),_:1})]),_:1}))}});function Je(e){let{rootEl:l,isSticky:n,layoutItemStyles:o}=e;const a=T(!1),t=T(0),i=w(()=>{const g=typeof a.value=="boolean"?"top":a.value;return[n.value?{top:"auto",bottom:"auto",height:void 0}:void 0,a.value?{[g]:L(t.value)}:{top:o.value.top}]});oe(()=>{D(n,g=>{g?window.addEventListener("scroll",p,{passive:!0}):window.removeEventListener("scroll",p)},{immediate:!0})}),le(()=>{document.removeEventListener("scroll",p)});let h=0;function p(){var y;const g=h>window.scrollY?"up":"down",u=l.value.getBoundingClientRect(),d=parseFloat((y=o.value.top)!=null?y:0),m=window.scrollY-Math.max(0,t.value-d),f=u.height+Math.max(t.value,d)-window.scrollY-window.innerHeight;u.height<window.innerHeight-d?(a.value="top",t.value=d):g==="up"&&a.value==="bottom"||g==="down"&&a.value==="top"?(t.value=window.scrollY+u.top,a.value=!0):g==="down"&&f<=0?(t.value=0,a.value="bottom"):g==="up"&&m<=0&&(t.value=u.top+m,a.value="top"),h=window.scrollY}return{isStuck:a,stickyStyles:i}}const Ke=100,Qe=20;function ee(e){const l=1.41421356237;return(e<0?-1:1)*Math.sqrt(Math.abs(e))*l}function te(e){if(e.length<2)return 0;if(e.length===2)return e[1].t===e[0].t?0:(e[1].d-e[0].d)/(e[1].t-e[0].t);let l=0;for(let n=e.length-1;n>0;n--){if(e[n].t===e[n-1].t)continue;const o=ee(l),a=(e[n].d-e[n-1].d)/(e[n].t-e[n-1].t);l+=(a-o)*Math.abs(a),n===e.length-1&&(l*=.5)}return ee(l)*1e3}function et(){const e={};function l(a){Array.from(a.changedTouches).forEach(t=>{var h;((h=e[t.identifier])!=null?h:e[t.identifier]=new ye(Qe)).push([a.timeStamp,t])})}function n(a){Array.from(a.changedTouches).forEach(t=>{delete e[t.identifier]})}function o(a){var g;const t=(g=e[a])==null?void 0:g.values().reverse();if(!t)throw new Error(`No samples for touch id ${a}`);const i=t[0],h=[],p=[];for(const u of t){if(i[0]-u[0]>Ke)break;h.push({t:u[0],d:u[1].clientX}),p.push({t:u[0],d:u[1].clientY})}return{x:te(h),y:te(p),get direction(){const{x:u,y:d}=this,[m,f]=[Math.abs(u),Math.abs(d)];return m>f&&u>=0?"right":m>f&&u<=0?"left":f>m&&d>=0?"down":f>m&&d<=0?"up":tt()}}}return{addMovement:l,endTouch:n,getVelocity:o}}function tt(){throw new Error}function at(e){let{isActive:l,isTemporary:n,width:o,touchless:a,position:t}=e;oe(()=>{window.addEventListener("touchstart",B,{passive:!0}),window.addEventListener("touchmove",M,{passive:!1}),window.addEventListener("touchend",x,{passive:!0})}),le(()=>{window.removeEventListener("touchstart",B),window.removeEventListener("touchmove",M),window.removeEventListener("touchend",x)});const i=w(()=>["left","right"].includes(t.value)),{addMovement:h,endTouch:p,getVelocity:g}=et();let u=!1;const d=T(!1),m=T(0),f=T(0);let y;function A(s,c){return(t.value==="left"?s:t.value==="right"?document.documentElement.clientWidth-s:t.value==="top"?s:t.value==="bottom"?document.documentElement.clientHeight-s:$())-(c?o.value:0)}function H(s){let c=arguments.length>1&&arguments[1]!==void 0?arguments[1]:!0;const v=t.value==="left"?(s-f.value)/o.value:t.value==="right"?(document.documentElement.clientWidth-s-f.value)/o.value:t.value==="top"?(s-f.value)/o.value:t.value==="bottom"?(document.documentElement.clientHeight-s-f.value)/o.value:$();return c?Math.max(0,Math.min(1,v)):v}function B(s){if(a.value)return;const c=s.changedTouches[0].clientX,v=s.changedTouches[0].clientY,b=25,V=t.value==="left"?c<b:t.value==="right"?c>document.documentElement.clientWidth-b:t.value==="top"?v<b:t.value==="bottom"?v>document.documentElement.clientHeight-b:$(),k=l.value&&(t.value==="left"?c<o.value:t.value==="right"?c>document.documentElement.clientWidth-o.value:t.value==="top"?v<o.value:t.value==="bottom"?v>document.documentElement.clientHeight-o.value:$());(V||k||l.value&&n.value)&&(u=!0,y=[c,v],f.value=A(i.value?c:v,l.value),m.value=H(i.value?c:v),p(s),h(s))}function M(s){const c=s.changedTouches[0].clientX,v=s.changedTouches[0].clientY;if(u){if(!s.cancelable){u=!1;return}const V=Math.abs(c-y[0]),k=Math.abs(v-y[1]);(i.value?V>k&&V>3:k>V&&k>3)?(d.value=!0,u=!1):(i.value?k:V)>3&&(u=!1)}if(!d.value)return;s.preventDefault(),h(s);const b=H(i.value?c:v,!1);m.value=Math.max(0,Math.min(1,b)),b>1?f.value=A(i.value?c:v,!0):b<0&&(f.value=A(i.value?c:v,!1))}function x(s){if(u=!1,!d.value)return;h(s),d.value=!1;const c=g(s.changedTouches[0].identifier),v=Math.abs(c.x),b=Math.abs(c.y);(i.value?v>b&&v>400:b>v&&b>3)?l.value=c.direction===({left:"right",right:"left",top:"down",bottom:"up"}[t.value]||$()):l.value=m.value>.5}const S=w(()=>d.value?{transform:t.value==="left"?`translateX(calc(-100% + ${m.value*o.value}px))`:t.value==="right"?`translateX(calc(100% - ${m.value*o.value}px))`:t.value==="top"?`translateY(calc(-100% + ${m.value*o.value}px))`:t.value==="bottom"?`translateY(calc(100% - ${m.value*o.value}px))`:$(),transition:"none"}:void 0);return{isDragging:d,dragProgress:m,dragStyles:S}}function $(){throw new Error}const nt=["start","end","left","right","top","bottom"],ot=Y()({name:"VNavigationDrawer",props:{color:String,disableResizeWatcher:Boolean,disableRouteWatcher:Boolean,expandOnHover:Boolean,floating:Boolean,modelValue:{type:Boolean,default:null},permanent:Boolean,rail:{type:Boolean,default:null},railWidth:{type:[Number,String],default:56},scrim:{type:[String,Boolean],default:!0},image:String,temporary:Boolean,touchless:Boolean,width:{type:[Number,String],default:256},location:{type:String,default:"start",validator:e=>nt.includes(e)},sticky:Boolean,...Me(),...Ee(),...ce(),...$e(),...se({tag:"nav"}),...ue()},emits:{"update:modelValue":e=>!0,"update:rail":e=>!0},setup(e,l){let{attrs:n,emit:o,slots:a}=l;const{isRtl:t}=re(),{themeClasses:i}=ie(e),{borderClasses:h}=Ae(e),{backgroundColorClasses:p,backgroundColorStyles:g}=K(F(e,"color")),{elevationClasses:u}=He(e),{mobile:d}=we(),{roundedClasses:m}=Pe(e),f=Re(),y=ne(e,"modelValue",null,_=>!!_),{ssrBootStyles:A}=X(),H=T(),B=T(!1),M=w(()=>e.rail&&e.expandOnHover&&B.value?Number(e.width):Number(e.rail?e.railWidth:e.width)),x=w(()=>Le(e.location,t.value)),S=w(()=>!e.permanent&&(d.value||e.temporary)),s=w(()=>e.sticky&&!S.value&&x.value!=="bottom");e.expandOnHover&&e.rail!=null&&D(B,_=>o("update:rail",!_)),e.disableResizeWatcher||D(S,_=>!e.permanent&&ke(()=>y.value=!_)),!e.disableRouteWatcher&&f&&D(f.currentRoute,()=>S.value&&(y.value=!1)),D(()=>e.permanent,_=>{_&&(y.value=!0)}),be(()=>{e.modelValue!=null||S.value||(y.value=e.permanent||!d.value)});const{isDragging:c,dragProgress:v,dragStyles:b}=at({isActive:y,isTemporary:S,width:M,touchless:F(e,"touchless"),position:x}),V=w(()=>{const _=S.value?0:e.rail&&e.expandOnHover?Number(e.railWidth):M.value;return c.value?_*v.value:_}),{layoutItemStyles:k,layoutRect:E,layoutItemScrimStyles:de}=ve({id:e.name,order:w(()=>parseInt(e.order,10)),position:x,layoutSize:V,elementSize:M,active:w(()=>y.value||c.value),disableTransitions:w(()=>c.value),absolute:w(()=>e.absolute||s.value&&typeof q.value!="string")}),{isStuck:q,stickyStyles:me}=Je({rootEl:H,isSticky:s,layoutItemStyles:k}),Z=K(w(()=>typeof e.scrim=="string"?e.scrim:null)),fe=w(()=>({...c.value?{opacity:v.value*.2,transition:"none"}:void 0,...E.value?{left:L(E.value.left),right:L(E.value.right),top:L(E.value.top),bottom:L(E.value.bottom)}:void 0,...de.value}));_e({VList:{bgColor:"transparent"}});function ge(){B.value=!0}function he(){B.value=!1}return W(()=>{const _=a.image||e.image;return r(Ve,null,[r(e.tag,z({ref:H,onMouseenter:ge,onMouseleave:he,class:["v-navigation-drawer",`v-navigation-drawer--${x.value}`,{"v-navigation-drawer--expand-on-hover":e.expandOnHover,"v-navigation-drawer--floating":e.floating,"v-navigation-drawer--is-hovering":B.value,"v-navigation-drawer--rail":e.rail,"v-navigation-drawer--temporary":S.value,"v-navigation-drawer--active":y.value,"v-navigation-drawer--sticky":s.value},i.value,p.value,h.value,u.value,m.value],style:[g.value,k.value,b.value,A.value,me.value]},n),{default:()=>{var U,j,G,J;return[_&&r("div",{key:"image",class:"v-navigation-drawer__img"},[a.image?(U=a.image)==null?void 0:U.call(a,{image:e.image}):r("img",{src:e.image,alt:""},null)]),a.prepend&&r("div",{class:"v-navigation-drawer__prepend"},[(j=a.prepend)==null?void 0:j.call(a)]),r("div",{class:"v-navigation-drawer__content"},[(G=a.default)==null?void 0:G.call(a)]),a.append&&r("div",{class:"v-navigation-drawer__append"},[(J=a.append)==null?void 0:J.call(a)])]}}),r(Se,{name:"fade-transition"},{default:()=>[S.value&&(c.value||y.value)&&!!e.scrim&&r("div",{class:["v-navigation-drawer__scrim",Z.backgroundColorClasses.value],style:[fe.value,Z.backgroundColorStyles.value],onClick:()=>y.value=!1},null)]})])}),{isStuck:q}}}),lt=O({__name:"NavBar",setup(e){const l=ae(),n=Be();function o(){De.request(Ie.logout).then(a=>{console.log(a),a.code=="200"&&(sessionStorage.clear(),l.drawer=!1,location.reload()),alert(a.msg)})}return(a,t)=>(I(),N(ot,{app:"","model-value":P(l).isDrawer,color:"red"},{default:C(()=>[r(qe,{density:"compact"},{default:C(()=>[r(R,{class:"mt-2","prepend-icon":"mdi-view-dashboard",title:"\u7CFB\u7EDF\u9762\u677F",value:"Home",onClick:t[0]||(t[0]=i=>P(n).push({name:"Home"}))}),r(R,{class:"mt-2","prepend-icon":"mdi-cube-outline",title:"\u5B9E\u4F8B\u7BA1\u7406",value:"Server",onClick:t[1]||(t[1]=i=>P(n).push({name:"Server"}))}),r(R,{class:"mt-2","prepend-icon":"mdi-console",title:"CMD",value:"Command",onClick:t[2]||(t[2]=i=>P(n).push({name:"Command"}))}),r(R,{class:"mt-2","prepend-icon":"mdi-cog-outline",title:"\u7CFB\u7EDF\u7BA1\u7406",value:"System",onClick:t[3]||(t[3]=i=>P(n).push({name:"System"}))}),r(R,{class:"mt-2","prepend-icon":"mdi-logout-variant",title:"\u767B\u51FA",onClick:t[4]||(t[4]=i=>o())})]),_:1})]),_:1},8,["model-value"]))}});const ut=Y()({name:"VMain",props:{scrollable:Boolean,...se({tag:"main"})},setup(e,l){let{slots:n}=l;const{mainStyles:o}=ze(),{ssrBootStyles:a}=X();return W(()=>r(e.tag,{class:["v-main",{"v-main--scrollable":e.scrollable}],style:[o.value,a.value]},{default:()=>{var t,i;return[e.scrollable?r("div",{class:"v-main__scroller"},[(t=n.default)==null?void 0:t.call(n)]):(i=n.default)==null?void 0:i.call(n)]}})),{}}}),rt={};function it(e,l){const n=Te("router-view");return I(),N(ut,{"mini-variant.sync":""},{default:C(()=>[r(n,{style:{"background-color":"#f3f3f3"}},{default:C(({Component:o,route:a})=>[r(Fe,{origin:"top left","leave-absolute":!0},{default:C(()=>[(I(),N(Ce(o),{key:a.path}))]),_:2},1024)]),_:1})]),_:1})}const st=Ze(rt,[["render",it]]);const ct=Y()({name:"VApp",props:{...Oe({fullHeight:!0}),...ue()},setup(e,l){let{slots:n}=l;const o=ie(e),{layoutClasses:a,layoutStyles:t,getLayoutItem:i,items:h,layoutRef:p}=Xe(e),{rtlClasses:g}=re();return W(()=>{var u;return r("div",{ref:p,class:["v-application",o.themeClasses.value,a.value,g.value],style:t.value},[r("div",{class:"v-application__wrap"},[(u=n.default)==null?void 0:u.call(n)])])}),{getLayoutItem:i,items:h,theme:o}}}),wt=O({__name:"Default",setup(e){return(l,n)=>(I(),N(ct,null,{default:C(()=>[r(Ge),r(lt),r(st)]),_:1}))}});export{wt as default};
