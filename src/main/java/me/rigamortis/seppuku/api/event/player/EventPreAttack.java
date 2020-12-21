package me.rigamortis.seppuku.api.event.player;

import me.rigamortis.seppuku.api.event.EventCancellable;
import net.minecraft.entity.Entity;

public class EventPreAttack extends EventCancellable {
    private Entity attacker;
    private Entity target;

    public EventPreAttack(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    public Entity getAttacker() {
        return this.attacker;
    }

    public Entity getTarget() {
        return this.target;
    }
}
