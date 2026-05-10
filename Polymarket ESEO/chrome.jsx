// Shared chrome: Windows-11 titlebar, Sidebar, Topbar, icons.
// Used by every artboard inside the design canvas.

// ─── Inline icons (Lucide-ish, stroke-only — no slop) ────────────────
const Icon = {
  Trends: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M2 12l4-4 3 3 5-6"/><path d="M11 5h2v2"/></svg>,
  Wallet: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" {...p}><rect x="2" y="4" width="12" height="9" rx="1.5"/><path d="M2 7h12"/><circle cx="11.5" cy="9.5" r=".7" fill="currentColor"/></svg>,
  Sport: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" {...p}><circle cx="8" cy="8" r="6"/><path d="M8 2v12M2 8h12"/></svg>,
  Cpu: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" {...p}><rect x="4" y="4" width="8" height="8" rx="1"/><path d="M2 6h2M2 10h2M12 6h2M12 10h2M6 2v2M10 2v2M6 12v2M10 12v2"/></svg>,
  Sparks: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" {...p}><path d="M8 2v3M8 11v3M2 8h3M11 8h3M4 4l2 2M10 10l2 2M12 4l-2 2M4 12l2-2"/></svg>,
  Plus: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" {...p}><path d="M8 3v10M3 8h10"/></svg>,
  Search: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" {...p}><circle cx="7" cy="7" r="4.5"/><path d="M11 11l3 3"/></svg>,
  Bell: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" {...p}><path d="M4 6.5a4 4 0 018 0v3l1.2 2H2.8L4 9.5v-3z"/><path d="M6.5 13a1.5 1.5 0 003 0"/></svg>,
  Dice: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" {...p}><rect x="2" y="2" width="12" height="12" rx="2"/><circle cx="5" cy="5" r=".8" fill="currentColor"/><circle cx="11" cy="5" r=".8" fill="currentColor"/><circle cx="8" cy="8" r=".8" fill="currentColor"/><circle cx="5" cy="11" r=".8" fill="currentColor"/><circle cx="11" cy="11" r=".8" fill="currentColor"/></svg>,
  Trophy: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M5 3h6v3a3 3 0 01-6 0V3z"/><path d="M5 4H3v1a2 2 0 002 2M11 4h2v1a2 2 0 01-2 2"/><path d="M6 9h4l-.5 3h-3z"/><path d="M5 13h6"/></svg>,
  Settings: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" {...p}><circle cx="8" cy="8" r="2"/><path d="M8 2v1.5M8 12.5V14M14 8h-1.5M3.5 8H2M12.2 3.8l-1.1 1.1M4.9 11.1l-1.1 1.1M12.2 12.2l-1.1-1.1M4.9 4.9L3.8 3.8"/></svg>,
  ArrowUp: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" {...p}><path d="M8 13V3M4 7l4-4 4 4"/></svg>,
  ArrowRight: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" {...p}><path d="M3 8h10M9 4l4 4-4 4"/></svg>,
  Check: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M3 8.5l3.5 3.5L13 4.5"/></svg>,
  X: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" {...p}><path d="M4 4l8 8M12 4L4 12"/></svg>,
  Coin: (p) => <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.4" {...p}><circle cx="8" cy="8" r="6"/><path d="M8 4v8M6 6.5h3a1.5 1.5 0 010 3H6.5"/></svg>,
};

function WindowChrome({ title = 'Polymarket Casino', children, casino = false }) {
  return (
    <div className="win">
      <div className="win-titlebar" style={casino ? {background: 'oklch(0.13 0.04 310)', borderBottomColor: 'oklch(0.84 0.16 86 / 0.2)'} : null}>
        <div className="title">
          <div className="title-logo" style={casino ? {background: 'linear-gradient(135deg, var(--gold), var(--neon))'} : null}></div>
          {title}
        </div>
        <div className="win-controls">
          <button title="Minimize">▁</button>
          <button title="Maximize">▢</button>
          <button className="close" title="Close">✕</button>
        </div>
      </div>
      {children}
    </div>
  );
}

function Sidebar({ active = 'markets', balance = '12,480.50' }) {
  const items = [
    { id: 'markets', icon: 'Trends', label: 'Markets' },
    { id: 'portfolio', icon: 'Wallet', label: 'Portfolio' },
    { id: 'create', icon: 'Plus', label: 'Create market' },
    { id: 'history', icon: 'Trophy', label: 'History' },
  ];
  const cats = [
    { id: 'tech', icon: 'Cpu', label: 'Tech / AI', count: 248 },
    { id: 'sport', icon: 'Sport', label: 'Sport', count: 412 },
    { id: 'absurd', icon: 'Sparks', label: 'Absurd', count: 87 },
  ];
  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="mark"></div>
        <div>
          <div style={{lineHeight: 1}}>Bet<span style={{color: 'var(--gold)'}}>Vault</span></div>
          <div style={{fontSize: 10, fontWeight: 400, color: 'var(--fg-3)', marginTop: 2}}>v0.4.2 · alpha</div>
        </div>
      </div>

      {items.map(it => {
        const I = Icon[it.icon];
        return (
          <button key={it.id} className={`nav-item ${active === it.id ? 'active' : ''}`}>
            <I className="ico"/>{it.label}
          </button>
        );
      })}

      <div className="nav-section-label">Categories</div>
      {cats.map(c => {
        const I = Icon[c.icon];
        return (
          <button key={c.id} className="nav-item">
            <I className="ico"/>{c.label}
            <span style={{marginLeft: 'auto', fontSize: 11, color: 'var(--fg-3)'}} className="mono tnum">{c.count}</span>
          </button>
        );
      })}

      <div style={{flex: 1}}></div>

      <button className={`nav-item casino ${active === 'casino' ? 'active' : ''}`}>
        <Icon.Dice className="ico"/>
        <span>Casino</span>
        <span className="badge">HOT</span>
      </button>

      <div style={{
        marginTop: 12,
        padding: 12,
        background: 'var(--bg-2)',
        borderRadius: 8,
        border: '1px solid var(--line-soft)',
      }}>
        <div style={{fontSize: 10, color: 'var(--fg-3)', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 4}}>Balance</div>
        <div style={{display: 'flex', alignItems: 'baseline', gap: 4}}>
          <span className="mono tnum" style={{fontSize: 18, fontWeight: 700, color: 'var(--fg-0)'}}>{balance}</span>
          <span style={{fontSize: 10, color: 'var(--gold)', fontWeight: 700}}>$PMC</span>
        </div>
        <div style={{fontSize: 11, color: 'var(--yes)', marginTop: 2}} className="tnum">+$240 · 24h</div>
      </div>
    </aside>
  );
}

function Topbar({ title = 'Markets', children, balance }) {
  return (
    <div className="topbar">
      <div style={{fontSize: 14, fontWeight: 600, color: 'var(--fg-0)'}}>{title}</div>
      <div className="search">
        <Icon.Search style={{width: 12, height: 12}}/>
        <span>Search markets, e.g. "GPT-5 release"</span>
      </div>
      {children}
      <div style={{flex: 1}}></div>
      {balance && (
        <div className="balance-pill">
          <div className="coin">P</div>
          <span className="mono tnum">{balance}</span>
          <span style={{color: 'var(--fg-3)', fontSize: 11}}>$PMC</span>
        </div>
      )}
      <button className="btn"><Icon.Bell style={{width: 12, height: 12}}/></button>
      <button className="btn-gold" style={{height: 32, fontSize: 11, padding: '0 14px'}}>+ Deposit</button>
    </div>
  );
}

Object.assign(window, { Icon, WindowChrome, Sidebar, Topbar });
