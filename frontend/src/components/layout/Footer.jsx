import React from 'react';

const Footer = () => {
  return (
    <footer className="py-4 px-8 border-t border-slate-200 bg-white text-center text-xs text-slate-400">
      <p>© {new Date().getFullYear()} HostelEase PG Management. All rights reserved.</p>
    </footer>
  );
};

export default Footer;
