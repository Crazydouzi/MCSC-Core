import{_ as x,g,a9 as S,T as c,H as m,G as _,L as b}from"./index.9753a940.js";function T(o){const r=x("useRender");r.render=o}function s(o){let r=arguments.length>1&&arguments[1]!==void 0?arguments[1]:"center center",i=arguments.length>2?arguments[2]:void 0;return g()({name:o,props:{disabled:Boolean,group:Boolean,hideOnLeave:Boolean,leaveAbsolute:Boolean,mode:{type:String,default:i},origin:{type:String,default:r}},setup(n,l){let{slots:a}=l;const t={onBeforeEnter(e){e.style.transformOrigin=n.origin},onLeave(e){if(n.leaveAbsolute){const{offsetTop:d,offsetLeft:f,offsetWidth:u,offsetHeight:y}=e;e._transitionInitialStyles={position:e.style.position,top:e.style.top,left:e.style.left,width:e.style.width,height:e.style.height},e.style.position="absolute",e.style.top=`${d}px`,e.style.left=`${f}px`,e.style.width=`${u}px`,e.style.height=`${y}px`}n.hideOnLeave&&e.style.setProperty("display","none","important")},onAfterLeave(e){if(n.leaveAbsolute&&(e==null?void 0:e._transitionInitialStyles)){const{position:d,top:f,left:u,width:y,height:v}=e._transitionInitialStyles;delete e._transitionInitialStyles,e.style.position=d||"",e.style.top=f||"",e.style.left=u||"",e.style.width=y||"",e.style.height=v||""}}};return()=>{const e=n.group?S:c;return m(e,{name:n.disabled?"":o,css:!n.disabled,...n.group?void 0:{mode:n.mode},...n.disabled?{}:t},a.default)}}})}function p(o,r){let i=arguments.length>2&&arguments[2]!==void 0?arguments[2]:"in-out";return g()({name:o,props:{mode:{type:String,default:i},disabled:Boolean},setup(n,l){let{slots:a}=l;return()=>m(c,{name:n.disabled?"":o,css:!n.disabled,...n.disabled?{}:r},a.default)}})}function h(){let o=arguments.length>0&&arguments[0]!==void 0?arguments[0]:"";const i=(arguments.length>1&&arguments[1]!==void 0?arguments[1]:!1)?"width":"height",n=_(`offset-${i}`);return{onBeforeEnter(t){t._parent=t.parentNode,t._initialStyle={transition:t.style.transition,overflow:t.style.overflow,[i]:t.style[i]}},onEnter(t){const e=t._initialStyle;t.style.setProperty("transition","none","important"),t.style.overflow="hidden";const d=`${t[n]}px`;t.style[i]="0",t.offsetHeight,t.style.transition=e.transition,o&&t._parent&&t._parent.classList.add(o),requestAnimationFrame(()=>{t.style[i]=d})},onAfterEnter:a,onEnterCancelled:a,onLeave(t){t._initialStyle={transition:"",overflow:t.style.overflow,[i]:t.style[i]},t.style.overflow="hidden",t.style[i]=`${t[n]}px`,t.offsetHeight,requestAnimationFrame(()=>t.style[i]="0")},onAfterLeave:l,onLeaveCancelled:l};function l(t){o&&t._parent&&t._parent.classList.remove(o),a(t)}function a(t){const e=t._initialStyle[i];t.style.overflow=t._initialStyle.overflow,e!=null&&(t.style[i]=e),delete t._initialStyle}}s("fab-transition","center center","out-in");s("dialog-bottom-transition");s("dialog-top-transition");s("fade-transition");const L=s("scale-transition");s("scroll-x-transition");s("scroll-x-reverse-transition");s("scroll-y-transition");s("scroll-y-reverse-transition");s("slide-x-transition");s("slide-x-reverse-transition");const A=s("slide-y-transition");s("slide-y-reverse-transition");const E=p("expand-transition",h()),B=p("expand-x-transition",h("",!0)),$=b({tag:{type:String,default:"div"}},"tag");export{L as V,E as a,B as b,A as c,$ as m,T as u};
